#include <z3.h>
#include <iostream>

class hello_fixedpoint_context {

    Z3_context m_ctx;
public:
    hello_fixedpoint_context(Z3_context c): m_ctx(c) {

    }

    void app_callback(Z3_func_decl f, unsigned n, Z3_ast const args[], Z3_ast* r) {
        Z3_ast res = Z3_mk_app(m_ctx, f, n, args);
        std::cout << "App: " << Z3_ast_to_string(m_ctx, res) << "\n";
        *r = res;
    }

    void assign_callback(Z3_func_decl f, unsigned n, Z3_ast const args[], 
                         unsigned num_out, Z3_ast const outs[]) {
        Z3_symbol s = Z3_get_decl_name(m_ctx, f);
        Z3_string name = Z3_get_symbol_string(m_ctx, s);

        for (unsigned i = 0; i < num_out; ++i) {
            std::cout << Z3_ast_to_string(m_ctx, outs[i]) << " ";
        }  

        std::cout << "<- " << name << " ";
        for (unsigned i = 0; i < n; ++i) {
            std::cout << Z3_ast_to_string(m_ctx, args[i]) << " ";
        }
        std::cout << "\n";
      
    }

    static void Z3_API reduce_assign_callback(
        void* state, Z3_func_decl f, unsigned n, Z3_ast const args[], 
        unsigned num_out, Z3_ast const outs[]) {
        hello_fixedpoint_context* ctx = static_cast<hello_fixedpoint_context*>(state);
        ctx->assign_callback(f, n, args, num_out, outs);        
    }

    static void Z3_API reduce_app_callback(
        void* state, Z3_func_decl f, unsigned n, Z3_ast const args[], Z3_ast* r)
    {
        hello_fixedpoint_context* ctx = static_cast<hello_fixedpoint_context*>(state);
        ctx->app_callback(f, n, args, r);
    }
};


void hello_fixedpoint() {
    Z3_config cfg = Z3_mk_config();
    Z3_context ctx = Z3_mk_context(cfg);
    Z3_fixedpoint dl = Z3_mk_fixedpoint(ctx);
    Z3_fixedpoint_inc_ref(ctx, dl);
   
    hello_fixedpoint_context d_context(ctx);
    Z3_fixedpoint_init(ctx, dl, &d_context);
    Z3_fixedpoint_set_reduce_assign_callback(ctx, dl, hello_fixedpoint_context::reduce_assign_callback);
    Z3_fixedpoint_set_reduce_app_callback(ctx, dl, hello_fixedpoint_context::reduce_app_callback);

    Z3_sort int_sort = Z3_mk_int_sort(ctx);
    Z3_sort bool_sort = Z3_mk_bool_sort(ctx);
    Z3_symbol P = Z3_mk_string_symbol(ctx, "P");
    Z3_func_decl pred = Z3_mk_func_decl(ctx, P, 1, &int_sort, bool_sort);
    Z3_ast n1 = Z3_mk_numeral(ctx, "1", int_sort);
    Z3_ast fact1 = Z3_mk_app(ctx, pred, 1, &n1);
    Z3_ast x = Z3_mk_bound(ctx, 0, int_sort);
    Z3_ast query = Z3_mk_app(ctx, pred, 1, &x);

    Z3_fixedpoint_add_rule(ctx, dl, fact1, 0);

    Z3_lbool r = Z3_fixedpoint_query(ctx, dl, query);
    if (r != Z3_L_UNDEF) { 
        Z3_ast a = Z3_fixedpoint_get_answer(ctx, dl);    
        std::cout << Z3_ast_to_string(ctx, a) << "\n";
    }
};
