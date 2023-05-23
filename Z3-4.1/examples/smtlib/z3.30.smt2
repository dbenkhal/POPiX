(set-option :auto-config true)
(set-option :produce-models true)

(set-option :print-success true) 
(set-option :produce-unsat-cores true) ; enable generation of unsat cores
(set-option :produce-models true) ; enable model generation
(set-option :mbqi true) ; enable model based quantifier instantiation
(set-option :produce-proofs true) ; enable proof generation
(declare-const x Int)
(set-option :produce-proofs false) ; error, cannot change this option after a declaration or assertion
(echo "before reset")
(reset)
(set-option :produce-proofs false) ; ok


