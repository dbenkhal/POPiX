module PartialOrderTheory

open Microsoft.Z3V3
open System.Collections.Generic
open Trail
      

type Node(t:Term) =    
   let mutable offset = 0
   let mutable index1 = 0
   let mutable index2 = 0
   let mutable index3 = 0
   member this.Term = t
   override this.Equals(other) = 
            match other with
            | null -> false
            | :? Node as n2 -> n2.Term = t
            | _ -> false           
   override this.GetHashCode() = t.GetHashCode()
   override this.ToString() = t.ToString()
   member this.Index1 with set i = index1 <- i and get() = index1
   member this.Index2 with set i = index2 <- i and get() = index2
   member this.Index3 with set i = index3 <- i and get() = index3
   member this.Offset with set i = offset <- i and get() = offset
 
   interface System.IComparable with
     member this.CompareTo(other:obj) = 
            match other with
            | :? Node as n2 -> t.CompareTo(n2)
            | _ -> -1   
    
                             
type Graph(trail:Trail) =
   let graph = Dictionary<Node, Node list>()
   let mutable offset = 0
   let mutable index = 0
             
   let reset() = 
       offset <- 1 + offset
       // deal with overflows once in a while
       if offset = 0 then
          offset <- 1
          for kv in graph do
              kv.Key.Offset <- 0
                  
   let is_marked (n:Node) = n.Offset = offset
   let set_mark (n:Node) = n.Offset <- offset

   let succ n = 
       match graph.TryGetValue n with
       | false, _ -> []
       | true, nodes -> nodes
      
   let rec bfs (queue:Queue<Node * Node list>) goal =
       if queue.Count = 0 then 
          None 
       else 
         let (m,path) = queue.Dequeue() 
         if goal m then 
            Some (List.rev (m::path))
         else if is_marked m then 
            bfs queue goal
         else 
            let path' = m::path
            set_mark m
            for k in succ m do
               queue.Enqueue((k,path'))
            bfs queue goal
            
   let on_stack (n:Node) = n.Index3 >= offset
   let set_on_stack (n:Node) = n.Index3 <- offset
   let set_off_stack (n:Node) = n.Index3 <- offset-1        
   let mutable stack = []
   let rec pop_until s n scc = 
       match s with
       | [] -> failwith "unexpected empty stack"
       | n'::s when n = n' -> 
         set_off_stack n         
         stack <- s; (n::scc)
       | n'::s -> 
         set_off_stack n'
         pop_until s n (n'::scc)
        
   let rec scc (n:Node) f = 
       n.Index1 <- index
       n.Index2 <- index
       index <- index + 1
       stack <- n::stack
       set_on_stack n
       set_mark n
       for m in succ n do
           if not (is_marked m) then
              scc m f
              n.Index2 <- min n.Index2 m.Index2
           else if on_stack m then
              n.Index2 <- min n.Index2 m.Index1
       if n.Index2 = n.Index1 then 
          f (pop_until stack n [])                                      
          
   let sccs f = 
       reset()
       index <- offset
       for kv in graph do
           if not (is_marked kv.Key) then
              scc kv.Key f
       done
       
   let add_node n =
       if not (graph.ContainsKey n) then
          graph.Add(n,[])
          trail.Add (fun () -> ignore(graph.Remove n))
          
   member this.Print() =
       for kv in graph do
           printf "%O -> %O\n" kv.Key kv.Value
                                             
   member this.AddEdge t1 t2 = 
       let n1 = Node(t1)
       let n2 = Node(t2)
       add_node n1
       add_node n2
       let nodes = graph.[n1]
       graph.[n1] <- n2::nodes
       trail.Add (fun () -> graph.[n1] <- List.tail graph.[n1])
                
   member this.Reachable t1 t2 = 
       let n1 = Node(t1)
       let n2 = Node(t2)
       let goal m = m = n2
       let queue = Queue()
       reset()
       queue.Enqueue((n1,[]))
       bfs queue goal       
       
   member this.Sccs f = sccs f

exception Break 
       
type PoTheory(ctx:Context) = 
    let th = ctx.MkTheory("po")
    let r = th.MkFuncDecl(ctx.MkSymbol("R"), [| ctx.MkIntSort(); ctx.MkIntSort() |], ctx.MkBoolSort())
    let trail = Trail()
    let g = Graph(trail)
    let mutable not_leqs = []
    let mutable terms = []
    
    let add_term t =
        terms <- t::terms
        trail.Add (fun () -> terms <- List.tail terms)
        
    let add_eq t1 t2 = 
        g.AddEdge t1 t2
        g.AddEdge t2 t1
                       
    let add_leq t1 t2 =         
        g.AddEdge t1 t2
           
    let add_not_leq t1 t2 = 
        trail.Add (fun () -> not_leqs <- List.tail not_leqs)
        not_leqs <- (t1,t2)::not_leqs

    let root (n:Node) = th.GetEqcRoot n.Term          

    // create theory axiom        
    let mk_conflict fmls = 
        let fmls = Array.ofList fmls
        let fml = ctx.MkNot(ctx.MkAnd fmls)
        // Printf.printf "Asserting %O\n" fml
        th.AssertTheoryAxiom fml
        
    let rec path2cstr (path:Node list) fmls =
        match path with
        | p1::p2::path when root p1 = root p2 -> 
          path2cstr (p2::path) (ctx.MkEq(p1.Term,p2.Term)::fmls)
        | p1::p2::path -> 
          path2cstr (p2::path) (ctx.MkApp(r, p1.Term, p2.Term)::fmls)
        | _ -> fmls


    let find_eqs equalities scc = 
        Printf.printf "find eqs %O\n" scc
        let add_eq n1 n2 eqs = 
            if root n1 = root n2 then
               eqs
            else
               (n1, n2)::eqs
        let rec find1 n nodes eqs = 
            match nodes with
            | [] -> eqs
            | n'::nodes -> 
              find1 n nodes (add_eq n n' eqs)              
        let rec find scc eqs = 
            match scc with 
            | [] -> eqs
            | n::scc -> find scc (find1 n scc eqs)        
        equalities := find scc !equalities
                                  
    let add_implied_eqs() = 
        let eqs = ref []
        g.Sccs (find_eqs eqs)
        for (t1,t2) in !eqs do
            let t1 = t1.Term
            let t2 = t2.Term
            let p1 = g.Reachable t1 t2 
            let p2 = g.Reachable t2 t1 
            assert (p1.IsSome && p2.IsSome)
            let c = path2cstr p1.Value []
            let c = path2cstr p2.Value c
            let c = ctx.MkNot(ctx.MkEq(t1,t2))::c
            mk_conflict c
       
     
    let check_not_leqs() = 
        try
            for (n1,n2) in not_leqs do                          
                match g.Reachable n1 n2 with
                | None -> ()
                | Some path -> 
                  let c = path2cstr path [ctx.MkNot(ctx.MkApp(r, n1,n2))]
                  mk_conflict c
                  raise Break
            done
        with
          | Break -> ()

    let is_leq (a:Term) =
        (a.GetKind() = TermKind.App) &&
        (a.GetAppDecl() = r) && 
        (a.GetAppArgs().Length = 2)

    //
    // FinalCheck runs scc algorithm to find equalities.
    // 
    // Also checks that nodes where negation of <: is asserted are not reachable.
    // 
    //    
    // NewAssignment updates the graph with positive constraints
    // It updates a negative list with negative constraints
    // 

    let FinalCheck() =         
        for t in terms do    // connect equal terms.
            let tr = th.GetEqcRoot t
            if tr <> t then
               add_eq t tr
        done
        check_not_leqs()    // check negations
        add_implied_eqs()   // propagate equalities
        true
    

    let NewAssignment (a:Term) v = 
        assert (is_leq a)
        let args = a.GetAppArgs()
        let t1, t2 = args.[0], args.[1]
        add_term t1; add_term t2
        if v then               
           g.AddEdge t1 t2
        else                    
           trail.Add (fun () -> not_leqs <- List.tail not_leqs)
           not_leqs <- (t1,t2)::not_leqs
              
    let initialize() =         
        th.FinalCheck <- (fun () -> FinalCheck())
        th.NewAssignment <- (fun a v -> NewAssignment a v)
        th.Push <- (fun () -> trail.Push())
        th.Pop <- (fun () -> trail.Pop())

                
    let _ = initialize()
            
    member this.R = r


let test() = 
    let ctx = use cfg = new Config() in new Context(cfg)         
    let po = PoTheory(ctx)
    let r = po.R
          
    let n1 = ctx.MkConst("n1", ctx.MkIntSort())
    let n2 = ctx.MkConst("n2", ctx.MkIntSort())
    let n3 = ctx.MkConst("n3", ctx.MkIntSort())


    do ctx.AssertCnstr (ctx.MkApp(r, n1, n2))
    do ctx.AssertCnstr (ctx.MkApp(r, n2, n3))

    // check transitivity
    do  Printf.printf "Expecting SAT: %O\n" (ctx.Check())
    do  ctx.Push()
    do  ctx.AssertCnstr (ctx.MkNot(ctx.MkApp(r, n1, n3)))
    do  Printf.printf "Expecting UNSAT: %O\n" (ctx.Check())
    do  ctx.Pop()

    // a cycle induces equalities
    do  ctx.Push()
    do  ctx.AssertCnstr (ctx.MkApp(r, n3, n1)) 
    do  Printf.printf "Expecting SAT: %O\n" (ctx.Check())
    do  ctx.AssertCnstr (ctx.MkNot(ctx.MkEq(n1,n2)))
    do  Printf.printf "Expecting UNSAT: %O\n" (ctx.Check())
    do  ctx.Pop()
    do  ctx.Dispose()

do test()


