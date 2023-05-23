
#include "bdd_table.h"
#include <vector>
#include <map>
#include <z3.h>

#define tout std::cout
#define TRACE(_tag_,_cmd_) /*  std::cout << "-------[" << _tag_ << "] " << __FUNCTION__ << ":" << __LINE__ << "\n"; _cmd_; */
#define SASSERT(_cond_)
#define UNREACHABLE();


typedef std::vector<unsigned> unsigned_vector;

// ------------------------------------
// bit-vector

typedef std::vector<bool> bit_vector;

static void unset(bit_vector& bv, unsigned idx) {
    if (idx < bv.size()) {
        bv[idx] = false;
    }
}

static bool get(bit_vector const& bv, unsigned idx) {
    return idx < bv.size() && bv[idx];
}

/** 
  \brief bdd_table implements a table represented using a Binary Decision diagram
         modulo a renaming of variables.
*/


class bdd_table {
    bdd      m_root;
    unsigned_vector m_renaming;

    /**
        \brief Check that \c rn is identical to \c m_renaming.
    */
    bool same_renaming(unsigned_vector const& rn) const {
        SASSERT(rn.size() == m_renaming.size());
        for (unsigned i = 0; i < rn.size(); ++i) {
            if (rn[i] != m_renaming[i]) {
                return false;
            }
        }
        return true;
    }

public:
    bdd_table() : m_root(bdd_false()) {}

    bdd_table(bdd root, unsigned_vector const& rn) 
        : m_root(root),
          m_renaming(rn)
        {
        }

    bdd_table(bdd_table const& other) : 
        m_root(other.m_root), 
        m_renaming(other.m_renaming) {
    }

    bdd_table& operator=(bdd_table const& other) {
        m_root     = other.m_root;
        m_renaming = other.m_renaming;
        return *this;
    }

    bdd_table(bdd_table const& other, bdd root):
        m_root(root),
        m_renaming(other.m_renaming) {
    }

    /**
        \brief Display the current BDD table.
    */
    std::ostream& display(std::ostream& out) const {
        out << "[";
        for (unsigned i = 0; i < m_renaming.size(); ++i) {
          out << m_renaming[i];
          if (i + 1 < m_renaming.size()) out << ":";
        }
        out << "] " << fddset << m_root << "\n";
        return out;
    }

    /**
        \brief form the union of two tables.
    */
    bdd_table operator|(bdd_table const& other) const {
        SASSERT(size() == other.size());
        if (same_renaming(other.m_renaming)) {
            return bdd_table(m_root | other.m_root, m_renaming);
        }
        if (m_root == bdd_false()) {
            return other;
        }
        if (other.m_root == bdd_false()) {
            return *this;
        }
        bddPair* pair = bdd_newpair();
        for (unsigned i = 0; i < size(); ++i) {
            fdd_setpair(pair, other.rename(i), rename(i));
        }
        bdd root2 = bdd_replace(other.root(), pair);
        bdd_freepair(pair);
        bdd_table result = bdd_table(root() | root2, m_renaming);
        TRACE("bdd_table", display(tout); other.display(tout); result.display(tout); );
        return result;
    }

    /** 
        \brief complement the current table 
    */
    bdd_table operator!() const {
        return bdd_table(!m_root, m_renaming);
    }

    /** 
        \brief Add a BDD as a disjunction 
    */
    void perform_or(bdd b) {
        m_root = bdd_or(m_root, b);
    }
    
    /** 
        \brief Conjoin a given BDD 
    */
    void perform_and(bdd b) {
        m_root = bdd_and(m_root, b);
    }
    
    /** 
        \brief check containment.
        NB. More efficient containment checks are possible with BDD packages.
    */
    bool contains(bdd b) {
        return 0 != (b == bdd_and(b, m_root));
    }
    
    bdd root() const { return m_root; }
    
    unsigned size() const { return static_cast<unsigned>(m_renaming.size()); }

    unsigned rename(unsigned i) const { return m_renaming[i]; }

    /**
        \brief Implement the join operator.

        NB. The can be optimized considerably by using caching.
    */
    bdd_table join(const bdd_table & other, size_t col_cnt, 
                   const unsigned * my_cols, const unsigned * other_cols,
                   unsigned_vector const& rn) const {
        
        bdd r1 = m_root;
        bdd r2 = other.root();
        unsigned size1 = size();
        unsigned size2 = other.size();                
        //
        // create renaming of variables into new signature.
        // 
        bddPair* pair = bdd_newpair();
        for (unsigned i = 0; i < size1; ++i) {
            fdd_setpair(pair, rename(i), rn[i]);
        }
        r1 = bdd_replace(r1,pair);
        bdd_freepair(pair);
        
        
        pair = bdd_newpair();
        for (unsigned i = 0; i < size2; ++i) {
            fdd_setpair(pair, other.rename(i), rn[size1 + i]);
        }
        r2 = bdd_replace(r2,pair);
        bdd_freepair(pair);
        
        bdd r = r1 & r2;
        
        //
        // create equality constraints between joined columns.
        // 
        for (unsigned i = 0; i < col_cnt; ++i) {
            unsigned v1 = my_cols[i];
            unsigned v2 = other_cols[i];
            bdd s1 = fdd_ithset(rn[v1]);
            bdd s2 = fdd_ithset(rn[size1 + v2]);
            r &= bdd_biimp(s1, s2);
        }        
        bdd_table result = bdd_table(r, rn);
        TRACE("bdd_table",
            display(tout);
            other.display(tout);
            for (unsigned i = 0; i < col_cnt; ++i) {
                tout << my_cols[i] << ":" << other_cols[i] << " ";
            }
            tout << "\n";
            result.display(tout);
            );
        return result;
    }

    /**
       \brief Compute the difference relation.

       \param other - the relation to be subtracted.
       \param col_cnt - number of columns used for the subtraction.
       \param my_cols - an array of column indies from the first relation.
       \param other_cols - an array of column indices from the subtracted relation.
       \param rn - a renaming that maps column indices to FDD indices.

       return {x: x \in S && ! \exists y: ( y \in R & pi_c1(x)= pi_d1(y) & .. & pi_cN(x)= pi_dN(y) ) }
       <=>
       return \forall y (eqs(x,y) => S & ! R)
    */

    bdd_table filter_negated(
        bdd_table const& other, size_t col_cnt, 
        const unsigned * my_cols, const unsigned * other_cols,
        unsigned_vector const& rn) const {
        bdd r1 = m_root;
        bdd r2 = other.root();
        unsigned size1 = size();
        unsigned size2 = other.size();
        unsigned_vector rn1;
        //
        // create renaming of variables into new signature.
        // 
        bddPair* pair = bdd_newpair();
        for (unsigned i = 0; i < size1; ++i) {
            fdd_setpair(pair, rename(i), rn[i]);
            rn1.push_back(rn[i]);
        }
        r1 = bdd_replace(r1,pair);
        bdd_freepair(pair);

        pair = bdd_newpair();
        for (unsigned i = 0; i < size2; ++i) {
            fdd_setpair(pair, other.rename(i), rn[size1 + i]);
        }
        r2 = bdd_replace(r2,pair);
        bdd_freepair(pair);        
        bdd r = r1 & !r2;
        //
        // create equality constraints between joined columns.
        // 
        for (unsigned i = 0; i < col_cnt; ++i) {
            unsigned v1 = my_cols[i];
            unsigned v2 = other_cols[i];
            bdd s1 = fdd_ithset(rn[v1]);
            bdd s2 = fdd_ithset(rn[size1 + v2]);
            r |= !bdd_biimp(s1, s2);
        }        
        bdd vars = bdd_true();            
        for (unsigned i = 0; i < size2; ++i) {
            vars &= fdd_ithset(rn[size1+i]);
        }
        r = bdd_forall(r, vars);
        return bdd_table(r, rn1);
    }
 
    /**
        \brief Perform projection.

        \param col_cnt - number of removed columns.
        \param removed_cols - an array of removed column indices.
        \param rn - a renaming that maps column indices to FDD indices.
    */
    bdd_table project(size_t col_cnt, const unsigned* removed_cols, unsigned_vector const& rn) const {
        bdd r = m_root;

        bit_vector bv;
        bv.resize(size(), true);
        for (unsigned i = 0; i < col_cnt; ++i) {
            unset(bv, removed_cols[i]);
        }
        
        bdd vars = bdd_true();            
        for (unsigned i = 0; i < col_cnt; ++i) {
            vars &= fdd_ithset(rename(removed_cols[i]));                
        }
        r = bdd_exist(r, vars);
        
        // form renaming from remaining variables to variables at offsets in new 
        // signature.
        bddPair* pair = bdd_newpair();
        unsigned i = 0, j = 0;
        for (; i < size(); ++i) {
            if (get(bv, i)) {
                fdd_setpair(pair, rename(i), rn[j]);
                ++j;
            }
        }
        r = bdd_replace(r, pair);
        bdd_freepair(pair);
        SASSERT(j == rn.size());        
        bdd_table result = bdd_table(r, rn);

        TRACE("bdd_table", 
        {
            display(tout);
            tout << "[";
            for (unsigned i = 0; i < col_cnt; ++i) {
                tout << removed_cols[i];
                if (i + 1 < col_cnt) tout << ":";
            }
            tout << "]\n";
            result.display(tout);
        }
        );
        return result;
    }  

    /**
        \brief Perform renaming.

        Renaming operations are decomposed to cyclic permutations.        

        \param cycle_len - length of permutation cycle.
        \param permutation_cycle - array of column indices whose contents are to be shifted cyclically.
    */
    bdd_table rename(size_t cycle_len, const unsigned * permutation_cycle) const {

        SASSERT(cycle_len > 1);
       
        unsigned_vector renaming(m_renaming);

        unsigned col1, col2;
        col1 = permutation_cycle[0];
        unsigned last_val = renaming[col1];        
        for (unsigned i = 1; i < cycle_len; ++i) {
            col2 = permutation_cycle[i];
            std::swap(last_val, renaming[col2]);
        }
        renaming[col1] = last_val;        
        bdd_table result = bdd_table(root(), renaming);
        TRACE("bdd_table", 
        {
            display(tout);
            tout << "[";
            for (unsigned i = 0; i < cycle_len; ++i) {
                tout << permutation_cycle[i];
                if (i + 1 < cycle_len) tout << ":";
            }
            tout << "]";
            result.display(tout);
        }
        );
        return result;
    }
};


struct ctx_ast {
    ctx_ast(Z3_context c, Z3_ast a) : m_context(c), m_ast(a) {}
    ctx_ast() : m_context(0), m_ast(0) {}
    Z3_context m_context;
    Z3_ast     m_ast;
};

struct ctx_ast_less {
    bool operator()(ctx_ast const& a, ctx_ast const& b) const {
        return Z3_get_ast_id(a.m_context, a.m_ast) <
               Z3_get_ast_id(b.m_context, b.m_ast);
    }
};


struct ctx_sort {
    ctx_sort(Z3_context c, Z3_sort s) : m_context(c), m_sort(s) {}
    ctx_sort() : m_context(0), m_sort(0) {}
    Z3_sort    m_sort;
    Z3_context m_context;
};


struct ctx_sort_less {
    bool operator()(ctx_sort const& a, ctx_sort const& b) const {
        return Z3_get_sort_id(a.m_context, a.m_sort) <
               Z3_get_sort_id(b.m_context, b.m_sort);
    }
};


typedef std::map<ctx_ast,  bdd_table, ctx_ast_less> ctx_ast_map;
typedef std::map<unsigned, unsigned_vector*> vector_map;

class bdd_context {
    Z3_context m_context;
    Z3_fixedpoint m_dl;
    unsigned   m_offset;
    ctx_ast_map  m_roots;
    vector_map m_offsets;

public:

    bdd_context(Z3_context c, Z3_fixedpoint dl) : m_context(c), m_offset(0) {
        bdd_init(1000,1000);
        Z3_fixedpoint_init(c, dl, this);
        Z3_fixedpoint_set_reduce_assign_callback(c, dl, reduce_assign_callback);
        Z3_fixedpoint_set_reduce_app_callback(c, dl, reduce_app_callback);
    }

    ~bdd_context() {
        std::map<unsigned,unsigned_vector*>::iterator it = m_offsets.begin(), end = m_offsets.end();
        for (; it != end; ++it) {
            delete(it->second);
        }
    }

    bdd get_bdd(Z3_ast a) {
        bdd_table result;
        ctx_ast k(m_context, a);
        result = m_roots.find(k)->second;
        return result.root();
    }

private:

    unsigned power2(unsigned n) {
        if (n > 30) {
            std::cout << "size not handled\n";
            exit(1);
        }
        return 1 << n;
    }

    /**
       \brief Given the sort of a relation, compute
              a unique offset into BuDDy's FDDs 
              to represent the relation.
    */
    void compute_renaming(Z3_sort s, unsigned_vector& renaming) {        
        std::map<unsigned, unsigned> next_offsets;
        update_renaming(s, next_offsets, renaming);
    }

    /**
       \brief Map column indices originating from sort \c s into FDD variable indices.
    */
    
    void update_renaming(
        Z3_sort s, 
        std::map<unsigned, unsigned>& next_offsets, 
        unsigned_vector& renaming) {                
        SASSERT(Z3_get_sort_kind(m_context, s) == Z3_RELATION_SORT);
        unsigned size = Z3_get_relation_arity(m_context, s);
        for (unsigned i = 0; i < size; ++i) {
            unsigned sz = get_sort_size(Z3_get_relation_column(m_context, s, i));
            unsigned next_offset = 0;
            unsigned_vector* offsets = 0;       
            if (m_offsets.find(sz) != m_offsets.end()) {
                offsets = m_offsets.find(sz)->second;
            }
            else { 
                offsets = new unsigned_vector();
                m_offsets[sz] = offsets;
                TRACE("bdd_table", tout << "new size: " << sz << "\n";);
            }           
            std::map<unsigned, unsigned>::iterator no_it = next_offsets.find(sz);
            if (no_it != next_offsets.end()) {
                next_offset = no_it->second;
            }
            else {
                next_offset = 0;
            }
            SASSERT(next_offset <= offsets->size());
            if (next_offset == offsets->size()) {
                offsets->push_back(m_offset);
                m_offset++;
                int dom = static_cast<int>(sz);
                fdd_extdomain(&dom, 1);
            }
            next_offsets[sz] = next_offset+1;
            SASSERT(next_offset < offsets->size());            
            renaming.push_back((*offsets)[next_offset]);
        }
        TRACE("bdd_table", tout << Z3_sort_to_string(m_context, s) << " - ";
              for (unsigned i = 0; i < renaming.size(); ++i) {
                  tout << i << " |-> " << renaming[i] << " ";
              }
              tout << "\n";
              );
    }

    unsigned get_sort_size(Z3_sort s) {
        Z3_sort_kind k = Z3_get_sort_kind(m_context, s);
        switch(k) {
        case Z3_BOOL_SORT:
            return 2;
        case Z3_BV_SORT: 
            return power2(Z3_get_bv_sort_size(m_context, s));
        default:
            std::cout << "Sort not handled " << Z3_sort_to_string(m_context, s) << "\n";
            exit(1);
        }
    }

    /**
       \brief evaluate the expression \c a to a BDD.

       \param t - a table represented as a BDD.
       \param var - the variable receiving the evaluated value.
       \param a - the expression to be evaluated.

       It is assumed that the argument is a constant or the 
       name of a column in the table \c t.
    */

    bdd eval(bdd_table& t, unsigned var, Z3_ast a) {
        switch (Z3_get_ast_kind(m_context, a)) {
        case Z3_APP_AST: {
            Z3_app app = Z3_to_app(m_context, a);
            Z3_func_decl f = Z3_get_app_decl(m_context, app);
            switch(Z3_get_decl_kind(m_context, f)) {
            case Z3_OP_TRUE: return fdd_ithvar(var, 1);
            case Z3_OP_FALSE: return fdd_ithvar(var, 0); 
            default:
                UNREACHABLE();
                break;
            }            
            break;
        }
        case Z3_NUMERAL_AST: {
            int val;
            Z3_get_numeral_int(m_context, a, &val);
            return fdd_ithvar(var, val);
        }
        case Z3_VAR_AST: {
            unsigned idx = Z3_get_index_value(m_context, a);
            return fdd_equals(t.rename(idx), var);
        }

        default:
            UNREACHABLE();
        }
        return bdd_false();
    }


    /**
       \brief filter a BDD table based on a relation.
    */
    bdd_table filter(bdd_table& t, Z3_ast cond) {
        Z3_app app = Z3_to_app(m_context, cond);
        Z3_func_decl f = Z3_get_app_decl(m_context, app);
        if(Z3_get_decl_kind(m_context, f) == Z3_OP_EQ) {
            return filter_eq(t, Z3_get_app_arg(m_context, app, 0), Z3_get_app_arg(m_context, app, 1));
        }
        std::cout << "only equality filters are supported in this sample\n"; 
        exit(1);
        return t;
    }

    /**
       \brief Filter a BDD table based on an equality.
    */

    bdd_table filter_eq(bdd_table& t, Z3_ast a1, Z3_ast a2) {
        if (Z3_get_ast_kind(m_context, a2) == Z3_VAR_AST) {
            std::swap(a1, a2);
        }
        if (Z3_get_ast_kind(m_context, a1) == Z3_VAR_AST) {
            unsigned var = t.rename(Z3_get_index_value(m_context, a1));
            bdd b = eval(t, var, a2);
            return bdd_table(t, b & t.root());
        }
        UNREACHABLE();
        return bdd_table();
    }

    /**
       \brief Compute the join of two tables represented by \c a1 and \c a2.
    */
    bdd_table join(Z3_func_decl f, Z3_ast a1, Z3_ast a2) {
        unsigned_vector my_cols, other_cols;
        Z3_sort s = Z3_get_range(m_context, f);
        unsigned_vector renaming;
        compute_renaming(s, renaming);
        unsigned num_params = Z3_get_decl_num_parameters(m_context, f);
        SASSERT(0 == (num_params % 2));
        for (unsigned i = 0; i < num_params; i += 2) {
            int c1 = Z3_get_decl_int_parameter(m_context, f, i);
            int c2 = Z3_get_decl_int_parameter(m_context, f, i + 1);
            my_cols.push_back(c1);
            other_cols.push_back(c2);
        }
        bdd_table t1 = eval(a1);                
        bdd_table t2 = eval(a2);
        return t1.join(t2, my_cols.size(), 
                       my_cols.data(), other_cols.data(),
                       renaming);
    }

    /**
       \brief Compute the difference of two tables represented by \c a1 and \c a2.
    */
    bdd_table negated_join(Z3_func_decl f, Z3_ast a1, Z3_ast a2) {
        unsigned_vector my_cols, other_cols;
        unsigned_vector renaming;
        std::map<unsigned, unsigned> next_offsets;
        update_renaming(Z3_get_sort(m_context, a1), next_offsets, renaming);
        update_renaming(Z3_get_sort(m_context, a2), next_offsets, renaming);
        unsigned num_params = Z3_get_decl_num_parameters(m_context, f);
        SASSERT(0 == (num_params % 2));
        for (unsigned i = 0; i < num_params; i += 2) {
            int c1 = Z3_get_decl_int_parameter(m_context, f, i);
            int c2 = Z3_get_decl_int_parameter(m_context, f, i + 1);
            my_cols.push_back(c1);
            other_cols.push_back(c2);
        }
        bdd_table t1 = eval(a1);                
        bdd_table t2 = eval(a2);
        return t1.filter_negated(t2, my_cols.size(), 
                       my_cols.data(), other_cols.data(),
                       renaming);
    }

    /**
       \brief Evaluate the relational algebra operation \c f to a table.
       
    */

    bdd_table eval_app(Z3_func_decl f, size_t num_args, Z3_ast const args[]) {
        bdd_table result;

        switch (Z3_get_decl_kind(m_context, f)) {
        case Z3_OP_RA_UNION:
        case Z3_OP_RA_WIDEN:
            {
                SASSERT(num_args == 2);
                bdd_table t1 = eval(args[0]);
                bdd_table t2 = eval(args[1]);                
                result = t1 | t2;
                break;
            }            
        case Z3_OP_RA_EMPTY:
            {
                SASSERT(num_args == 0);
                Z3_sort s = Z3_get_range(m_context, f);
                unsigned_vector renaming;
                compute_renaming(s, renaming);
                result = bdd_table(bdd_false(), renaming);
                break;
            }
        case Z3_OP_RA_JOIN:
            SASSERT(num_args == 2);
            result = join(f, args[0], args[1]);
            break;
        case Z3_OP_RA_FILTER:
            {
                SASSERT(num_args == 1);
                SASSERT(Z3_get_decl_num_parameters(m_context, f) == 1);
                Z3_ast cond = Z3_get_decl_ast_parameter(m_context, f, 0);
                result = filter(eval(args[0]), cond);
                break;
            }
        case Z3_OP_RA_PROJECT: 
            {
                unsigned_vector cols, renaming;
                Z3_sort s = Z3_get_range(m_context, f);
                compute_renaming(s, renaming);
                unsigned num_params = Z3_get_decl_num_parameters(m_context, f);
                SASSERT(num_params > 0);
                for (unsigned i = 0; i < num_params; ++i) {
                    int c1 = Z3_get_decl_int_parameter(m_context, f, i);
                    cols.push_back(c1);
                }
                result = eval(args[0]).project(cols.size(), cols.data(), renaming);
                break;
            }
            
        case Z3_OP_RA_RENAME:
            {
                unsigned_vector cols;
                unsigned num_params = Z3_get_decl_num_parameters(m_context, f);
                SASSERT(num_params > 0);
                for (unsigned i = 0; i < num_params; ++i) {
                    int c1 = Z3_get_decl_int_parameter(m_context, f, i);
                    cols.push_back(c1);
                }
                result = eval(args[0]).rename(cols.size(), cols.data());
                break;
            }
        case Z3_OP_RA_COMPLEMENT:
            {
                result = !eval(args[0]);
                break;
            }
        case Z3_OP_RA_STORE:
            {
                SASSERT(num_args > 0);
                bdd_table t1 = eval(args[0]);
                bdd v = bdd_true();
                SASSERT(num_args == t1.size() + 1);
                for (unsigned i = 0; i < num_args-1; ++i) {
                    Z3_ast a = args[i+1]; 
                    v = v & eval(t1, t1.rename(i), a);
                }
                result = bdd_table(t1, t1.root() | v);
                break;
            }
        case Z3_OP_RA_NEGATION_FILTER:
            SASSERT(num_args == 2);
            result = negated_join(f, args[0], args[1]);
            break;
        case Z3_OP_RA_CLONE:
            result = eval(args[0]);
            break;
        case Z3_OP_RA_IS_EMPTY:
        case Z3_OP_RA_SELECT:
            UNREACHABLE();
            break;
        default:
            std::cout << "case not handled\n";
            UNREACHABLE();
        }        
        TRACE("bdd_table_eval", 
              {
                  Z3_symbol s = Z3_get_decl_name(m_context, f);
                  Z3_string name = Z3_get_symbol_string(m_context, s);                  
                  tout << name << " ";
                  for (unsigned i = 0; i < num_args; ++i) {
                      tout << Z3_ast_to_string(m_context, args[i]) << " ";
                  }
                  tout << "\n";
                  result.display(tout);
              });
        return result;
    }    

    bdd_table eval(Z3_ast a) {       
        bdd_table result;
        ctx_ast ctx_a(m_context, a);
        ctx_ast_map::iterator it = m_roots.find(ctx_a);
        if (it != m_roots.end()) {
            return it->second;
        }
        Z3_app app = Z3_to_app(m_context, a);
        Z3_func_decl f = Z3_get_app_decl(m_context, app);
        unsigned num_args = Z3_get_app_num_args(m_context, app);
        std::vector<Z3_ast> args;
        for (unsigned i = 0; i < num_args; ++i) {
            args.push_back(Z3_get_app_arg(m_context, app, i));
        }
        return eval_app(f, args.size(), args.data());
    }

    bool is_empty(Z3_ast a) {
        return 0 != (eval(a).root() == bdd_false());
    }

    /**
       \brief A callback registered with the Z3 API for evaluating expressions.
    */
    
    void app_callback(Z3_func_decl f, unsigned num_args, Z3_ast const args[], Z3_ast* r) {

        TRACE("bdd_table", 
              {
                  Z3_symbol s = Z3_get_decl_name(m_context, f);
                  Z3_string name = Z3_get_symbol_string(m_context, s);                  
                  tout << name << " ";
                  for (unsigned i = 0; i < num_args; ++i) {
                      tout << Z3_ast_to_string(m_context, args[i]) << " ";
                  }
                  tout << "\n";
              });

        switch(Z3_get_decl_kind(m_context, f)) {
        case Z3_OP_RA_IS_EMPTY: {
            SASSERT(num_args == 1);
            *r = is_empty(args[0])?Z3_mk_true(m_context):Z3_mk_false(m_context);
            break;
        }
        case Z3_OP_RA_SELECT: {
            SASSERT(num_args > 0);
            bdd_table t1 = eval(args[0]);
            bdd b = t1.root();
            
            SASSERT(num_args == t1.size() + 1);
            for (unsigned i = 0; i < num_args-1; ++i) {
                b &= eval(t1, t1.rename(i), args[i+1]);
            }
            *r = (bdd_false() != b)?Z3_mk_true(m_context):Z3_mk_false(m_context);
            break;
        }
        default:
            // let caller handle this.
            break;
        }
    }

    /**
       \brief A callback registered with the Z3 API for destructively updating 
              state as relations are evaluated.
    */
    void assign_callback(Z3_func_decl f, unsigned num_args, Z3_ast const args[], 
                         unsigned num_out, Z3_ast const outs[]) {        
        TRACE("bdd_table", 
              {
                  Z3_context c = m_context;
                  Z3_symbol s = Z3_get_decl_name(c, f);
                  Z3_string name = Z3_get_symbol_string(c, s);
                  
                  for (unsigned i = 0; i < num_out; ++i) {
                      tout << Z3_ast_to_string(c, outs[i]) << " ";
                  }  
                  
                  tout << "<- " << name << " ";
                  for (unsigned i = 0; i < num_args; ++i) {
                      tout << Z3_ast_to_string(c, args[i]) << " ";
                  }
                  tout << "\n";
              });
        SASSERT(num_out > 0);
        bdd_table r = eval_app(f, num_args, args);
        ctx_ast ca0(m_context, outs[0]);
        if (num_out > 1) {
            ctx_ast ca1(m_context, outs[1]);
            switch(Z3_get_decl_kind(m_context, f)) {
            case Z3_OP_RA_UNION:
            case Z3_OP_RA_WIDEN:
                if (m_roots.find(ca0) != m_roots.end() && 
                    m_roots.find(ca0)->second.root() == r.root()) {
                    m_roots[ca1] = bdd_table(r, bdd_false());
                }
                else {
                    m_roots[ca1] = r;
                }
                break;
            case Z3_OP_RA_CLONE:
                m_roots[ca1] = r;
                break;
            default:
                UNREACHABLE();
                break;
            }
        }
        m_roots[ca0] = r;
    }

    static void Z3_API reduce_app_callback(void* state, Z3_func_decl f, unsigned num_args, Z3_ast const args[], Z3_ast* r) {
        bdd_context* ctx = static_cast<bdd_context*>(state);
        ctx->app_callback(f, num_args, args, r);
    }


    static void Z3_API reduce_assign_callback(void* state, Z3_func_decl f, unsigned num_args, Z3_ast const args[], unsigned num_out, Z3_ast const outs[]) {
        bdd_context* ctx = static_cast<bdd_context*>(state);
        ctx->assign_callback(f, num_args, args, num_out, outs);
    }
};


static bdd_context* g_bdd_ctx = 0;

void bdd_table_init(Z3_context ctx, Z3_fixedpoint dl) {
   g_bdd_ctx = new bdd_context(ctx, dl);    
}

void bdd_table_finalize() {
    delete g_bdd_ctx;
}


bdd get_bdd(Z3_ast e) {
    return g_bdd_ctx->get_bdd(e);
}




