#include "bdd_table.h"
#include <z3.h>
#include <vector>

#define TRACE(_tag_, _cmd_)
#define SASSERT(_cond_)

/*
    simple example: the LT relation given by:
     LT(x,y) :- LT(x,z), LT(z,y) .
     LT(x,y) :- succ(x,y) .
     succ(x,y,0,x,y,1) .
     succ(x,1,0,x,0,1) .
     succ(1,0,0,0,1,1) .  (for arity 3).
    Then succ is a large relation. 
    LT is only quadratic in the size of succ, and furthermore
    with symbolic joins we compute it in log(n) steps.

    Take the variant:
     LT(x,y) :- succ(x,z), LT(z,y) .
     LT(x,y) :- succ(x,y) .

    succ is still a large relation. 
    Computing LT does not converge quickly.
    Profiling should show that 
     LT(x,y) :- succ(x,z), LT(z,y) .
    is used frequently.
    Resolve the rule against itself:
     LT(x,y) :- succ(x,z), succ(z,u), LT(u,y) .
    Introduce the table succ2 and rules:
    succ2(x,y) :- succ(x,z), succ(z,y) .
    LT(x,y) :- succ2(x,z), LT(z,y) .
*/

static Z3_func_decl mk_2nary_rel(Z3_context ctx, Z3_fixedpoint dl, char const* name, unsigned n) {
    Z3_symbol nm = Z3_mk_string_symbol(ctx, name);
    unsigned arity = 2*n;
    std::vector<Z3_sort> domain;
    for (unsigned i = 0; i < arity; ++i) {
        domain.push_back(Z3_mk_bool_sort(ctx));
    }
    Z3_sort b = Z3_mk_bool_sort(ctx);
    Z3_func_decl pred = Z3_mk_func_decl(ctx, nm, arity, domain.data(), b);
    Z3_fixedpoint_register_relation(ctx, dl, pred);
    return pred;
}

//     succ(x,y,0,x,y,1) .
//     succ(x,1,0,x,0,1) .
//     succ(1,0,0,0,1,1) .  (for arity 3).

static void assert_succ(Z3_context ctx, Z3_fixedpoint dl, Z3_func_decl succ, unsigned n, unsigned num_vars) {
    SASSERT(num_vars < n);
    std::vector<Z3_ast> args;
    Z3_ast tr = Z3_mk_true(ctx);
    Z3_ast fl = Z3_mk_false(ctx);
    Z3_sort s = Z3_mk_bool_sort(ctx);
    for (unsigned j = 0; j < num_vars; ++j) {
        args.push_back(Z3_mk_bound(ctx, j, s));
    }
    args.push_back(fl);
    for (unsigned j = num_vars+1; j < n; ++j) {
        args.push_back(tr);
    }
    for (unsigned j = 0; j < num_vars; ++j) {
        args.push_back(Z3_mk_bound(ctx, j, s));
    }
    args.push_back(tr);
    for (unsigned j = num_vars+1; j < n; ++j) {
        args.push_back(fl);
    }
    SASSERT(args.size() == 2*n);
    Z3_ast p = Z3_mk_app(ctx, succ, static_cast<unsigned>(args.size()), args.data());

    TRACE("bdd_table", tout << Z3_ast_to_string(ctx, p) << "\n";);
    
    Z3_fixedpoint_add_rule(ctx, dl, p, 0);
}

static void assert_succ(Z3_context ctx, Z3_fixedpoint dl, Z3_func_decl succ, unsigned n) {
    for (unsigned i = 0; i < n; ++i) {
        assert_succ(ctx, dl, succ, n, i);
    }
}

//     LT(x,y) :- LT(x,z), LT(z,y) .    if !use_succ
//     LT(x,y) :- succ(x,z), LT(z,y) .  if use_succ
//     LT(x,y) :- succ(x,y) .
//     succ(..)

static void assert_lt_tc(Z3_context ctx, Z3_fixedpoint dl, Z3_func_decl lt, Z3_func_decl succ, unsigned n, bool use_succ) {
    if (n == 0) {
        return;
    }
    Z3_sort s = Z3_mk_bool_sort(ctx);
    std::vector<Z3_ast> xy, xz, zy;
    for(unsigned i = 0; i < n; ++i) {
        xy.push_back(Z3_mk_bound(ctx, i, s));
        xz.push_back(Z3_mk_bound(ctx, i, s));
        zy.push_back(Z3_mk_bound(ctx, i+2*n, s));
    }
    for(unsigned i = 0; i < n; ++i) {
        xy.push_back(Z3_mk_bound(ctx, n+i, s));
        xz.push_back(Z3_mk_bound(ctx, 2*n+i, s));
        zy.push_back(Z3_mk_bound(ctx, n+i, s));
    }
    Z3_ast lt_x_y = Z3_mk_app(ctx, lt, static_cast<unsigned>(xy.size()), xy.data());
    Z3_ast lt_z_y = Z3_mk_app(ctx, lt, static_cast<unsigned>(zy.size()), zy.data());
    Z3_ast succ_x_y = Z3_mk_app(ctx, succ, static_cast<unsigned>(xy.size()), xy.data());

    Z3_ast body1[2] = { 0, lt_z_y };
    if (use_succ) {
        body1[0] = Z3_mk_app(ctx, succ, static_cast<unsigned>(xz.size()), xz.data());
    }
    else {
        body1[0] = Z3_mk_app(ctx, lt, static_cast<unsigned>(xz.size()), xz.data());        
    }
    Z3_ast body2[1] = { succ_x_y };
    Z3_fixedpoint_add_rule(ctx, dl, Z3_mk_implies(ctx, Z3_mk_and(ctx, 2, body1), lt_x_y), 0);
    TRACE("bdd_table", tout << Z3_ast_to_string(ctx, lt_x_y) << " :- ";
                       tout << Z3_ast_to_string(ctx, body1[0]) << ", ";
                       tout << Z3_ast_to_string(ctx, body1[1]) << "\n";);

    Z3_fixedpoint_add_rule(ctx, dl, Z3_mk_implies(ctx, body2[0], lt_x_y), 0);
    TRACE("bdd_table", tout << Z3_ast_to_string(ctx, lt_x_y) << " :- ";
                       tout << Z3_ast_to_string(ctx, body2[0]) << "\n";);

    assert_succ(ctx, dl, succ, n);
}

static void test_lt(unsigned n, bool use_succ, bool use_bdd, Z3_config cfg) {
    Z3_context ctx = Z3_mk_context(cfg);   
    Z3_fixedpoint dl = Z3_mk_fixedpoint(ctx);
    Z3_fixedpoint_inc_ref(ctx, dl);
    if (use_bdd) {
        bdd_table_init(ctx, dl);
    }

    if (n > 0) {
        Z3_func_decl succ = mk_2nary_rel(ctx, dl, "succ", n);
        Z3_func_decl lt   = mk_2nary_rel(ctx, dl, "<", n);

        assert_lt_tc(ctx, dl, lt, succ, n, use_succ);
        std::vector<Z3_ast> args;
        Z3_sort s = Z3_mk_bool_sort(ctx);
        for (unsigned i = 0; i < 2*n; ++i) {
            args.push_back(Z3_mk_bound(ctx, i, s));
        }
        Z3_ast query = Z3_mk_app(ctx, lt, static_cast<unsigned>(args.size()), args.data());
        Z3_lbool r = Z3_fixedpoint_query(ctx, dl, query);
        
        if (use_bdd && r != Z3_L_UNDEF) {
            Z3_ast a = Z3_fixedpoint_get_answer(ctx, dl);
            std::cout << Z3_ast_to_string(ctx, a) << "\n";
            bdd b = get_bdd(a);
            fdd_printset(b);   
        }
    }
    if (use_bdd) {
        bdd_table_finalize();
    }
    Z3_del_config(cfg);
    Z3_fixedpoint_dec_ref(ctx, dl);
    Z3_del_context(ctx); 
}


// assert P(1)
// query  P(x)

static void test1() {
    Z3_config cfg = Z3_mk_config();
    Z3_context ctx = Z3_mk_context(cfg);   
    Z3_fixedpoint dl = Z3_mk_fixedpoint(ctx);
    Z3_fixedpoint_inc_ref(ctx, dl);
    bdd_table_init(ctx, dl);
    Z3_sort bv_sort = Z3_mk_bv_sort(ctx, 4);
    Z3_sort bool_sort = Z3_mk_bool_sort(ctx);
    Z3_symbol P = Z3_mk_string_symbol(ctx, "P");
    Z3_func_decl pred = Z3_mk_func_decl(ctx, P, 1, &bv_sort, bool_sort);
    Z3_fixedpoint_register_relation(ctx,dl, pred);
    Z3_ast n1 = Z3_mk_numeral(ctx, "1", bv_sort);
    Z3_ast fact1 = Z3_mk_app(ctx, pred, 1, &n1);
    Z3_ast x = Z3_mk_bound(ctx, 0, bv_sort);
    Z3_ast query = Z3_mk_app(ctx, pred, 1, &x);
    Z3_fixedpoint_add_rule(ctx, dl, fact1, 0);
    Z3_lbool r = Z3_fixedpoint_query(ctx, dl, query);
    Z3_ast a = Z3_fixedpoint_get_answer(ctx, dl);
    std::cout << Z3_ast_to_string(ctx, a) << "\n";
    bdd b = get_bdd(a);
    fdd_printset(b);    
    bdd_table_finalize();
    Z3_del_config(cfg);
    Z3_fixedpoint_dec_ref(ctx, dl);
    Z3_del_context(ctx);
}

// assert P(1)
// assert P(2)
// query P(x)

void test2() {
    Z3_config cfg = Z3_mk_config();
    Z3_context ctx = Z3_mk_context(cfg);   
    Z3_fixedpoint dl = Z3_mk_fixedpoint(ctx);
    Z3_fixedpoint_inc_ref(ctx, dl);
    bdd_table_init(ctx, dl);
    Z3_sort bv_sort = Z3_mk_bv_sort(ctx, 4);
    Z3_sort bool_sort = Z3_mk_bool_sort(ctx);
    Z3_symbol P = Z3_mk_string_symbol(ctx, "P");
    Z3_func_decl pred = Z3_mk_func_decl(ctx, P, 1, &bv_sort, bool_sort);
    Z3_fixedpoint_register_relation(ctx, dl, pred);
    Z3_ast n1 = Z3_mk_numeral(ctx, "1", bv_sort);
    Z3_ast fact1 = Z3_mk_app(ctx, pred, 1, &n1);
    Z3_fixedpoint_register_relation(ctx, dl, pred);
    Z3_fixedpoint_add_rule(ctx, dl, fact1, 0);
    Z3_ast n2 = Z3_mk_numeral(ctx, "2", bv_sort);
    Z3_ast fact2 = Z3_mk_app(ctx, pred, 1, &n2);
    Z3_fixedpoint_add_rule(ctx, dl, fact2, 0);

    Z3_ast x = Z3_mk_bound(ctx, 0, bv_sort);
    Z3_ast query = Z3_mk_app(ctx, pred, 1, &x);
    Z3_lbool r = Z3_fixedpoint_query(ctx, dl, query);
    Z3_ast a  = Z3_fixedpoint_get_answer(ctx, dl);
    std::cout << Z3_ast_to_string(ctx, a) << "\n";
    bdd b = get_bdd(a);
    fdd_printset(b);    
    bdd_table_finalize();
    Z3_del_config(cfg);
    Z3_fixedpoint_dec_ref(ctx, dl);
    Z3_del_context(ctx);
}

void test_bdd_table(int argc, char* argv[]) {
    Z3_config cfg = Z3_mk_config();
    unsigned n = 4;
    bool use_succ = true;
    bool use_bdd = true;
    if (argc > 1) {
        n = atoi(argv[1]);
    }
    if (argc > 2) {
        use_succ = 0 != atoi(argv[2]);
    }
    if (argc > 3) {
        if (strcmp("smt",argv[3]) == 0) {
            std::cout << "use smt relation\n";
            Z3_set_param_value(cfg, "DL_DEFAULT_RELATION", "smt_relation1");
            //enable_trace("smt_context");
            //enable_trace("before_search");
            //enable_trace("quant_elim");
            //enable_trace("der");
            //enable_trace("simplifier");
        }
        use_bdd = 0 != atoi(argv[3]);
    }


    std::cout << "width: " << n << " use succ: " << (use_succ?"true":"false") << " use bdd: " << (use_bdd?"true":"false") << "\n";

    test_lt(n, use_succ, use_bdd, cfg);

}
