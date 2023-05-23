namespace Microsoft.Z3V3

module Util = 


  // This is an active pattern for terms.
  let (|App|Numeral|Quantifier|Bound|) (term:Term) = 
      match term.GetKind() with
      | TermKind.App ->        
        App(term.GetAppDecl(), term.GetAppArgs())
      | TermKind.Numeral ->
        Numeral(term.GetNumeralString(), term.GetSort())
      | TermKind.Quantifier -> 
        Quantifier (term.GetQuantifier())
      | TermKind.Var ->
        Bound(term.GetVarIndex(), term.GetSort())  
      | _ -> 
        raise (System.Exception (Printf.sprintf "Term not recognized %O" term))
        
  let (|Kind|) (d:FuncDecl) = Kind(d.GetKind())
 
  let match_binary k (term:Term) = 
      match term with
      | App(Kind(k1), [| a1; a2 |]) when k1 = k -> Some(a1,a2)
      | _ -> None
        
  let (|Eq|_|) (term:Term) = match_binary DeclKind.Eq term
  let (|Mod|_|) (term:Term) = match_binary DeclKind.Mod term
  let (|Add|_|) (term:Term) = match_binary DeclKind.Add term

  
  // 
  // Abstract the terms listed in env.
  // The terms are replaced by bound variables.
  //

  let Abstract (z3:Context, env, term) = 
      let cache = System.Collections.Generic.Dictionary<_,_>()
      let rec find base_ env term = 
          match env with 
          | [] -> None 
          | t::_ when t.Equals(term) -> Some base_ 
          | _::env' -> find (base_ + 1) env' term
      let rec abs_mem (base_:int) term = 
          match cache.TryGetValue(term) with
          | true, v -> v
          | false, _ -> 
            let v = abs_rec base_ term
            cache.Add(term, v)
            v
      and abs_rec base_ term = 
          match find base_ env term with
          | Some index -> (z3.MkBound ((uint32) index, z3.GetSort(term)) ) 
          | None -> 
          match term with
          | App(decl, args) -> 
            let args = Array.init args.Length (fun i -> abs_mem base_ (args.[i])) in
            z3.MkApp(decl, args)
          | Quantifier q -> 
            cache.Clear()
            let num_bound = Array.length q.Sorts     in
            let base_ = num_bound + base_ in
            let body  = abs_mem base_ q.Body in
            let pats  = Array.map (abs_pattern base_) q.Patterns in
            cache.Clear()
            z3.MkQuantifier(q.IsForall, q.Weight, pats, [||], q.Sorts, q.Names, body)
          | Numeral(_,_) -> term
          | Bound(idx,ty)  -> term
      and abs_pattern _base pat = 
          let terms = z3.GetPatternTerms pat in
          let terms = Array.map (abs_mem _base) terms in
          z3.MkPattern terms
      in
      abs_mem 0 term     
  
  let AbstractOfList(z3, env: System.Collections.Generic.List<Term>, term) = 
      Abstract(z3, List.init env.Count (fun i -> env.[i]), term)

  let AbstractOfArray(z3, env: Term[], term) = 
      Abstract(z3, List.init env.Length (fun i -> env.[i]), term)
             
  let rec FoldPost(f, v, t) = 
      match t with
      | App(d, args) -> f(Array.fold (fun s t -> FoldPost(f,s,t)) v args, t)
      | Bound _ -> f(v,t)
      | Numeral _ -> f(v,t)
      | Quantifier(q) -> f(FoldPost(f, v, q.Body), t)
      
  let rec FoldPre(f, v, t) = 
      match t with
      | App(d, args) -> Array.fold (fun s t -> FoldPre(f,s,t)) (f(t,v)) args
      | Bound _ -> f(v,t)
      | Numeral _ -> f(v,t)
      | Quantifier(q) -> FoldPre(f, f(v,t), q.Body)


          
     
  //     
  // This is a general auxiliary function for memoizing recursive functions.
  // 
  let memoize f_rec = 
      let cache = System.Collections.Generic.Dictionary<_,_>()
      let rec f1 x = 
          match cache.TryGetValue(x) with
          | true,v -> v
          | false,_ ->
            let v = f_rec f1 x
            cache.[x] <- v
            v
      in 
      f1
          
  //  
  // instantiate de-Bruijn indices in body by terms.
  // The order of terms is reversed during instantiatiation to take into account
  // Curry style binding of quantified variables.
  // The last term in the array terms instantiates Bound(0), 
  // the second last by Bound(1) etc.
  //
  let Instantiate(z3 :Context, instTerms, body) = 
      let numInst = Array.length instTerms
      let rec inst1 offset = 
          let inst_rec inst term = 
              match term with
              | App(decl, args) -> z3.MkApp(decl, Array.map inst args) 
              | Quantifier q -> 
                let offset = Array.length q.Sorts + offset
                let body = inst1 offset q.Body
                let inst_p pat = 
                    let terms = z3.GetPatternTerms pat in
                    let terms = Array.map (inst1 offset) terms in
                    z3.MkPattern terms
                let pats = Array.map inst_p q.Patterns
                z3.MkQuantifier(q.IsForall, q.Weight, pats, [||], q.Sorts, q.Names, body)            
              | Numeral(_,_) -> term
              | Bound(idx,s) when (int)idx >= offset && (int)idx < offset + numInst -> 
                instTerms.[numInst - ((int)idx-offset) - 1]
              | Bound _ -> term
          in 
          memoize inst_rec
      in
      inst1 0 body

  let ReplaceClosedTerm(z3 :Context, x, t, s) = 
      let rec rep_rec rep term = 
          if x = term then t else
          match term with
          | App(decl, args) -> z3.MkApp(decl, Array.map rep args)
          | Quantifier q -> 
            let body = rep q.Body
            z3.MkQuantifier(q.IsForall, q.Weight, q.Patterns, [||], q.Sorts, q.Names, body)
          | _ -> term
      in 
      memoize rep_rec s