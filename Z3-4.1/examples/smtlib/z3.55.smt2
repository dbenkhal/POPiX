(set-option :auto-config true)
(set-option :produce-models true)

(define-sort Set (T) (Array T Bool))
(define-sort IList () (List Int))
(define-sort List-Set (T) (Array (List T) Bool))
(define-sort I () Int)

(declare-const s1 (Set I))
(declare-const s2 (List-Set Int))
(declare-const a I)
(declare-const l IList)

(assert (= (select s1 a) true))
(assert (= (select s2 l) false))
(check-sat)
(get-model)