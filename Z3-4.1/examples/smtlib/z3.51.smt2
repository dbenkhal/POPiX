(set-option :auto-config true)
(set-option :produce-models true)

(set-option :mbqi true)
(declare-fun n () Int)
(declare-fun a_1 () Int)
(declare-fun f (Int) Int)
(declare-fun g_1 (Int) Int)
(assert (> n 0))
(assert (forall ((i Int))
        (=> (and (<= 0 i) (<= i n))
            (and (= (f 0) 0)
                 (= (f 2) 2)
                 (<= 0 (f i))
                 (<= (f i) 2)
                 (=> (= (f i) 2) (= i n))
                 (=> (= (f i) 0)
                     (or (= (f (+ i 1)) 1) (= (f (+ i 1)) 2)))
                 (=> (= (f i) 1)
                     (or (= (f (+ i 1)) 1) (= (f (+ i 1)) 2)))
                 (= (g_1 0) 0)
                 (=> (= (f i) 0) (= (g_1 (+ i 1)) 0))
                 (=> (= (f i) 1) (= (g_1 (+ i 1)) (+ (g_1 i) 1)))
                 (=> (= (f i) 2)
                     (= (g_1 (+ i 1)) (g_1 i)))
                 (=> (= (f i) 1) (< (g_1 i) a_1))
                 (=> (= (f i) 2) 
                     (and (>= (g_1 i) a_1) (> (g_1 i) 2)))))))
(check-sat)
(get-model)

(echo "Property does not hold for n > 1")
(assert (> n 1))
(check-sat)
