
#include "buddy/fdd.h"
#include "buddy/bdd.h"
#include <z3.h>

void bdd_table_init(Z3_context ctx, Z3_fixedpoint fp);
void bdd_table_finalize();
bdd get_bdd(Z3_ast e);

void test_bdd_table(int argc, char* argv[]);
