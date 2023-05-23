// The namespace Z3.Quotations
///
/// F# quotations and Z3
/// 
///
///  This module contains translation utilies from quoted F# expressions into Z3.
///  To assign domain specific translations for selected domains, the translator
///  can get plugins. The plugin defines a custom translation of some expressions.
///  It may use a callback into the main translator.
///

 

namespace Microsoft.Z3V3.Quotations

open Microsoft.FSharp.Quotations
open Microsoft.FSharp.Linq
open Microsoft.FSharp.Linq.QuotationEvaluation

module Debug = 
  let debug = ref true

///
/// The Logic module contains functions representing logical operators.
/// The 'forall' and 'exists' operators encode quantifiers. They take 
/// a function from 'a to bool and produce a boolean valued term. This argument
/// should be a lambda expression, and the lambda bound variables become bound by 
/// the quantifier.
/// The function 'declare' can be used to introduce a set of identifiers that get used
/// as variables. The declared variables become free, so that Z3 uses the same names
/// for the resulting constants.
/// forall_p and exists_p introduce quantifiers with patterns.
/// Use 'underspecified' when introducing an un-interpreted function symbol.
/// F# implements structural equality. This is not defined on function spaces. 
/// The translation from F# quotations into Z3 terms treats function spaces
/// as arrays, and equality is well-defined on arrays. The function 'equals' can
/// therefore be used when the built-in structural equality does not suffice.
/// 
module Logic =

  let forall (pred : ('a -> bool)) = raise (System.Exception "can't evaluate forall")
  let exists (pred : ('a -> bool)) = raise (System.Exception "can't evaluate exists")
  let declare   (pred : 'a)        = raise (System.Exception "can't evaluate declaration of variables")
  let declare_preds (pred : 'a)    = raise (System.Exception "can't evaluate declaration of predicates")
  let forall_p (pat : ('a -> 'b)) (pred : 'a -> bool) = raise (System.Exception "can't evaluate forall")
  let exists_p (pat : ('a -> 'b)) (pred : 'a -> bool) = raise (System.Exception "can't evaluate exists")
  let big_and  n (pred : (int -> bool)) = raise (System.Exception "can't evaluate big-and")
  let underspecified<'a>()        = raise (System.Exception "can't evaluate underspecified value"): 'a
  let (equals) = fun (a:'a) (b:'a) -> (raise (System.Exception "can't evaluate equals") : bool)
  
  [<ReflectedDefinition>]
  let ite pred t e = if pred then t else e
  [<ReflectedDefinition>]
  let (==>) a b = (not a) || b 

///
/// The theory of arrays contains the basic combinators:
/// K - for the constant array.
/// store  - to update arrays.
/// select - to index into arrays. 
/// map    - to map functions over array ranges.
/// default_value - to access default range values of arrays.
///
/// The signature also includes 'I', the identify combinator.
/// This combinator allows encoding lambda terms.
///
module Arrays = 
  let K x           = (); fun y -> x
  let I             = (fun x -> x)
  let store a i v   = ();fun j -> if i = j then v else a j
  let select a i    = ();a i  
  let map1 f x      = ();fun i -> f (x i) 
  let map2 f x y    = ();fun i -> f (x i) (y i)
  let map3 f x y z  = ();fun i -> f (x i) (y i) (z i)
  let default_value (f: 'a -> 'b) = (raise (System.Exception "can't evaluate default value"): 'b)

  [<ReflectedDefinition>]
  let singleton x = store (K false) x true
  [<ReflectedDefinition>]
  let union x y = map2 (||) x y
  [<ReflectedDefinition>]
  let intersect x y = map2 (&&) x y
  [<ReflectedDefinition>]
  let complement x = map1 not x
  [<ReflectedDefinition>]
  let difference x y = intersect x (complement y)
  [<ReflectedDefinition>]
  let subset_eq x y = Logic.equals (K false) (difference x y) 
  [<ReflectedDefinition>]
  let is_finite x = default_value x = false
  [<ReflectedDefinition>]
  let bag_union x y = map2 (+) x y
  [<ReflectedDefinition>]
  let bag_intersect x y = map2 (min) x y    
  [<ReflectedDefinition>]
  let bag_empty() = K 0I
  [<ReflectedDefinition>]
  let subbag_eq x y = Logic.equals (K true) (map2 (<=) x y)
  
module Translate = 
  open Microsoft.Z3V3
  open Microsoft.FSharp.Reflection
  
  open DerivedPatterns
            
  let (|ReflectedCall|_|) e = 
       match e with
       | Patterns.Call(_,mi,args) -> 
          match mi with
          | MethodWithReflectedDefinition(md) -> Some (md, args)
          | _ -> None
       | Patterns.PropertyGet(_,pi,args) ->
          match pi with
          | PropertyGetterWithReflectedDefinition(pd) -> Some (pd, args)
          | _ -> None
       | _ -> None
       
  let (|SpecificProp|_|) e = 
      match e with
      | Patterns.PropertyGet(None, pi1, _) -> 
        (fun tm -> 
         match tm with
         | Patterns.PropertyGet(None,pi2,args2) when pi1 = pi2 -> 
           Some(args2)
         | _ -> None)
      | _ -> 
        invalidArg "templateParameter" "the parameter is not a recognized method name"
               
                   
  let (|GenericEqualityQ|_|) = (|SpecificCall|_|) <@ LanguagePrimitives.GenericEquality @>
  let (|EqualsQ|_|)    = (|SpecificCall|_|) <@ ( = ) @>
  let (|NotEqQ|_|) = (|SpecificCall|_|) <@ ( <> ) @>
  let (|NotQ|_|) =  (|SpecificCall|_|) <@ not   @>
  let (|BitwiseAndQ|_|) = (|SpecificCall|_|) <@ ( &&& ) @>
  let (|BitwiseOrQ|_|) = (|SpecificCall|_|) <@ ( ||| ) @>
  let (|BitwiseXorQ|_|) = (|SpecificCall|_|) <@ ( ^^^ ) @>
  let (|BitwiseNotQ|_|) = (|SpecificCall|_|) <@ ( ~~~ ) @>  
  let (|GreaterQ|_|)   = (|SpecificCall|_|) <@ ( > ) @>
  let (|GreaterEqQ|_|) = (|SpecificCall|_|) <@ ( >=) @>
  let (|LessQ|_|)      = (|SpecificCall|_|) <@ ( <)  @>
  let (|LessEqQ|_|) = (|SpecificCall|_|) <@ ( <=) @>  
  let (|NegQ|_|) = (|SpecificCall|_|) <@ ( ~-) : int -> int @>
  let (|PlusQ|_|)       = (|SpecificCall|_|) <@ ( + ) @>
  let (|DivideQ|_|)     = (|SpecificCall|_|) <@ ( / ) @> 
  let (|MinusQ|_|)      = (|SpecificCall|_|) <@ ( - ) @>
  let (|MultiplyQ|_|)   = (|SpecificCall|_|) <@ ( * ) @>
  let (|ModuloQ|_|)     = (|SpecificCall|_|) <@ ( % ) @>
  let (|ShiftLeftQ|_|)  = (|SpecificCall|_|) <@ ( <<< ) @>
  let (|ShiftRightQ|_|) = (|SpecificCall|_|) <@ ( >>> ) @>
  let (|ConvSByteQ|_|)  = (|SpecificCall|_|) <@ sbyte @>
  let (|ConvInt16Q|_|)  = (|SpecificCall|_|) <@ int16 @>
  let (|ConvInt32Q|_|)  = (|SpecificCall|_|) <@ int32 @>
  let (|ConvInt64Q|_|)  = (|SpecificCall|_|) <@ int64 @>
  let (|ConvByteQ|_|)   = (|SpecificCall|_|) <@ byte @>
  let (|ConvUInt16Q|_|) = (|SpecificCall|_|) <@ uint16 @>
  let (|ConvUInt32Q|_|) = (|SpecificCall|_|) <@ uint32 @>
  let (|ConvUInt64Q|_|) = (|SpecificCall|_|) <@ uint64 @>


  let mk_typed_app (m:System.Reflection.MethodInfo) type_args = 
      m.GetGenericMethodDefinition().MakeGenericMethod(type_args)
      
  let rec mk_lambdas xs e = 
      match xs with
      | [] -> e
      | x::xs -> Expr.Lambda(x,mk_lambdas xs e)
      
  let rec get_lambdas e xs = 
      match e with
      | Patterns.Lambda(x,e) -> get_lambdas e (x::xs)
      | Patterns.Call(None, m, args) -> 
        let xs = List.rev xs
        assert (List.forall2 (fun x a -> Expr.Var(x) = a) xs args)
        (m,xs)
      | _ -> failwith "Could not match method call"
  

  // \x . \y . \z . (m x y z) -> (m, [x; y; z])
  // m : (a -> (b -> (c -> R)))
  let mk_args parameters vars = 
      assert (List.length parameters = List.length vars)      
      List.map2 (fun (p:System.Reflection.ParameterInfo) (v:Var) -> 
                      Var(v.ToString(), p.ParameterType)) parameters vars
            
      
  let mk_call (m:System.Reflection.MethodInfo) args = 
      assert (
          let ps = Array.toList (m.GetParameters())
          (List.length ps = List.length args) &&
          (List.forall2 (fun (p:System.Reflection.ParameterInfo) (e:Expr) -> p.ParameterType = e.Type) ps args))
      Expr.Call(m, args)
          
  let mk_typed_fun_call e type_args = 
      let m, xs = get_lambdas e []
      let m = mk_typed_app m type_args
      let xs = mk_args (Array.toList (m.GetParameters())) xs 
      let args  = List.map Expr.Var xs
      mk_lambdas xs (mk_call m args)
          
  let apply_generic e type_args args = 
      match e with
      | DerivedPatterns.Lambdas(vars,Patterns.Call(None, m1,_)) ->
        let m2 = mk_typed_app m1 type_args
        mk_call m2 args
      | _ -> failwith "unexpected function"; e
      
      
      
  type env_type = Var -> Term
  type plugin_context = { 
          mke : Expr -> Term; 
          mkt : System.Type -> Sort;
          env : env_type;
          env0 : unit -> env_type;
          mke_env : env_type -> Expr -> Term;
          add_env : env_type -> Var -> Term -> env_type;
        }

  ///
  /// The translation module uses plugins to define translation of 
  /// specific theories.
  /// For example, the translation of machine integers is handled by the 
  /// BvPlugin.
  /// 
  [<AbstractClass>]
  type IPlugin() = class
       abstract mk_expr : Expr -> (plugin_context -> Term) option
       abstract mk_type : System.Type -> ((System.Type -> Sort) -> Sort) option
    end 

  ///
  /// Plugin for integers and rational numbers.
  /// 
  type NumPlugin(z3:Context) = 
       inherit IPlugin()
       let int_sort = z3.MkIntSort()
       let real_sort = z3.MkRealSort()
       let is_int ty = typeof<System.Numerics.BigInteger> = ty 
       let is_rational ty = typeof<Microsoft.FSharp.Math.BigNum> = ty
       let is_num ty = is_int ty || is_rational ty
       let (|IFromString|_|) = (|SpecificCall|_|) <@ NumericLiteralI.FromString  @>
       let (|IFromInt|_|)    = (|SpecificCall|_|) <@ NumericLiteralI.FromInt32  @>
       let (|IFromInt64|_|)  = (|SpecificCall|_|) <@ NumericLiteralI.FromInt64  @>
       let (|IFromOne|_|)    = (|SpecificCall|_|) <@ NumericLiteralI.FromOne  @>
       let (|IFromZero|_|)   = (|SpecificCall|_|) <@ NumericLiteralI.FromZero  @>
       let (|NFromInt|_|)    = (|SpecificCall|_|) <@ NumericLiteralN.FromInt32  @>
       let (|NFromInt64|_|)  = (|SpecificCall|_|) <@ NumericLiteralN.FromInt64  @>
       let (|NFromOne|_|)    = (|SpecificCall|_|) <@ NumericLiteralN.FromOne  @>
       let (|NFromZero|_|)   = (|SpecificCall|_|) <@ NumericLiteralN.FromZero  @>      
       let (|MinQ|_|)        = (|SpecificCall|_|) <@ (min) @>    
       let (|MaxQ|_|)        = (|SpecificCall|_|) <@ (max) @> 
       (*
       let (|ZeroPropN|_|) = (|SpecificProp|_|) <@ Microsoft.FSharp.Math.BigNum.Zero @>
       let (|OnePropN|_|) = (|SpecificProp|_|) <@ Microsoft.FSharp.Math.BigNum.One @>
       let (|ZeroPropI|_|) = (|SpecificProp|_|) <@ Microsoft.FSharp.Math.BigInt.Zero @>
       let (|OnePropI|_|) = (|SpecificProp|_|) <@ Microsoft.FSharp.Math.BigInt.One @>
       *)
       let mk_cmp cmp e1 e2 = 
           Some (fun c -> let e1, e2 = c.mke e1, c.mke e2 in z3.MkIte(cmp(e1,e2),e1,e2))
       override this.mk_expr e = 
           match e with
           | NegQ(_,_,[e1])        when is_num(e1.Type) -> Some (fun c -> z3.MkUnaryMinus(c.mke e1))
           | PlusQ(_,_,[e1;e2])    when is_num(e1.Type) -> Some (fun c -> z3.MkAdd(c.mke e1, c.mke e2))
           | MultiplyQ(_,_,[e1;e2])  when is_num(e1.Type) -> Some (fun c -> z3.MkMul(c.mke e1, c.mke e2))
           | DivideQ(_,_,[e1;e2])    when is_num(e1.Type) -> Some (fun c -> z3.MkDiv(c.mke e1, c.mke e2))
           | ModuloQ(_,_,[e1;e2])    when is_num(e1.Type) -> Some (fun c -> z3.MkMod(c.mke e1, c.mke e2))
           | MinusQ(_,_,[e1;e2])     when is_num(e1.Type) -> Some (fun c -> z3.MkSub(c.mke e1, c.mke e2))
           | GreaterQ(_,_,[e1;e2])   when is_num(e1.Type) -> Some (fun c -> z3.MkGt(c.mke e1, c.mke e2))
           | GreaterEqQ(_,_,[e1;e2]) when is_num(e1.Type) -> Some (fun c -> z3.MkGe(c.mke e1, c.mke e2))
           | LessQ(_,_,[e1;e2])      when is_num(e1.Type) -> Some (fun c -> z3.MkLt(c.mke e1, c.mke e2))
           | LessEqQ(_,_,[e1;e2])    when is_num(e1.Type) -> Some (fun c -> z3.MkLe(c.mke e1, c.mke e2))
           | IFromInt(_,_,[DerivedPatterns.Int32(i)])     -> Some (fun c -> z3.MkNumeral(i, int_sort))
           | IFromInt64(_,_,[DerivedPatterns.Int64(i)]) -> Some (fun c -> z3.MkNumeral(i, int_sort))
           | IFromString(_,_,[DerivedPatterns.String(s)]) -> Some (fun c -> z3.MkNumeral(s, int_sort))
           | IFromOne(_,_,_)  -> Some (fun c -> z3.MkNumeral(1, int_sort))
           | IFromZero(_,_,_) -> Some (fun c -> z3.MkNumeral(0, int_sort))
           | NFromInt(_,_,[DerivedPatterns.Int32(i)]) -> Some (fun c -> z3.MkNumeral(i, real_sort))
           | NFromInt64(_,_,[DerivedPatterns.Int64(i)]) -> Some (fun c -> z3.MkNumeral(i, real_sort))
           | NFromOne(_,_,_)  -> Some (fun c -> z3.MkNumeral(1, real_sort))
           | NFromZero(_,_,_) -> Some (fun c -> z3.MkNumeral(0, real_sort))    
           | MinQ(_,_,[e1;e2]) when is_num(e1.Type) -> mk_cmp z3.MkLt e1 e2
           | MaxQ(_,_,[e1;e2]) when is_num(e1.Type) -> mk_cmp z3.MkGt e1 e2
           //| ZeroPropN(_) -> Some (fun c -> z3.MkNumeral(0, real_sort)) // deprecated ops.
           //| OnePropN(_) -> Some (fun c -> z3.MkNumeral(1, real_sort))
           //| ZeroPropI(_) -> Some (fun c -> z3.MkNumeral(0, int_sort))
           //| OnePropI(_) -> Some (fun c -> z3.MkNumeral(1, int_sort))                      
           | _ -> None
     
       override this.mk_type ty = 
           if is_int ty then Some (fun _ -> int_sort) else 
           if is_rational ty then Some (fun _ -> real_sort) else None
    
  ///
  /// Plugin for bit-vectors
  ///
  type BvPlugin(z3:Context) = class
       inherit IPlugin()
       let is_ubv ty = 
           ty = typeof<uint8> ||
           ty = typeof<uint16> ||
           ty = typeof<uint32> ||
           ty = typeof<uint64> 

       let is_sbv ty = 
           ty = typeof<int8> ||
           ty = typeof<int16> ||
           ty = typeof<int32> ||
           ty = typeof<int64> 

       let is_bv ty = is_ubv ty || is_sbv ty
       let get_num_bytes ty = 
           match ty with
           | _ when ty = typeof<uint8>  -> 8ul
           | _ when ty = typeof<uint16> -> 16ul
           | _ when ty = typeof<uint32> -> 32ul
           | _ when ty = typeof<uint64> -> 64ul
           | _ when ty = typeof<int8>   -> 8ul
           | _ when ty = typeof<int16>  -> 16ul
           | _ when ty = typeof<int32>  -> 32ul
           | _ when ty = typeof<uint64> -> 64ul
           | _ -> raise (System.Exception "unexpected type")
           
       let convert2unsigned nb_dst (e:Expr) = 
           let nb_src = get_num_bytes e.Type 
           if nb_dst = nb_src then 
             Some (fun c -> c.mke e)
           else if nb_dst > nb_src then 
             Some (fun c -> z3.MkBvZeroExt(nb_dst-nb_src,c.mke e))
           else 
             Some (fun c -> z3.MkBvExtract(nb_dst,0ul, c.mke e)) 
             
       let convert2signed nb_dst (e:Expr) = 
           let nb_src = get_num_bytes e.Type 
           if nb_dst = nb_src then 
             Some (fun c -> c.mke e)
           else if nb_dst > nb_src then 
             Some (fun c -> z3.MkBvSignExt(nb_dst-nb_src,c.mke e))
           else 
             Some (fun c -> z3.MkBvExtract(nb_dst,0ul, c.mke e)) //? check 
 
       let (|MinQ|_|)      = (|SpecificCall|_|) <@ (min)  @>    
       let (|MaxQ|_|)      = (|SpecificCall|_|) <@ (max)  @>
       
       let mk_cmp cmp e1 e2 = 
           Some (fun c -> let e1, e2 = c.mke e1, c.mke e2 in z3.MkIte(cmp(e1,e2),e1,e2))
                                                        
       override this.mk_expr e = 
           match e with
           | NegQ(_,_,[e1])           when is_bv(e1.Type)  -> Some (fun c -> z3.MkBvNeg(c.mke e1))
           | PlusQ(_,_,[e1;e2])       when is_bv(e1.Type)  -> Some (fun c -> z3.MkBvAdd(c.mke e1, c.mke e2))
           | MultiplyQ(_,_,[e1;e2])   when is_bv(e1.Type)  -> Some (fun c -> z3.MkBvMul(c.mke e1, c.mke e2))
           | MinusQ(_,_,[e1;e2])      when is_bv(e1.Type)  -> Some (fun c -> z3.MkBvSub(c.mke e1, c.mke e2))
           | DivideQ(_,_,[e1;e2])     when is_ubv(e1.Type) -> Some (fun c -> z3.MkBvUdiv(c.mke e1, c.mke e2))
           | ModuloQ(_,_,[e1;e2])     when is_ubv(e1.Type) -> Some (fun c -> z3.MkBvUrem(c.mke e1, c.mke e2))
           | GreaterQ(_,_,[e1;e2])    when is_ubv(e1.Type) -> Some (fun c -> z3.MkBvUgt(c.mke e1, c.mke e2))
           | GreaterEqQ(_,_,[e1;e2])  when is_ubv(e1.Type) -> Some (fun c -> z3.MkBvUge(c.mke e1, c.mke e2))
           | LessQ(_,_,[e1;e2])       when is_ubv(e1.Type) -> Some (fun c -> z3.MkBvUlt(c.mke e1, c.mke e2))
           | LessEqQ(_,_,[e1;e2])     when is_ubv(e1.Type) -> Some (fun c -> z3.MkBvUle(c.mke e1, c.mke e2))
           | DivideQ(_,_,[e1;e2])     when is_sbv(e1.Type) -> Some (fun c -> z3.MkBvSdiv(c.mke e1, c.mke e2))
           | ModuloQ(_,_,[e1;e2])     when is_sbv(e1.Type) -> Some (fun c -> z3.MkBvSrem(c.mke e1, c.mke e2))
           | GreaterQ(_,_,[e1;e2])    when is_sbv(e1.Type) -> Some (fun c -> z3.MkBvSgt(c.mke e1, c.mke e2))
           | GreaterEqQ(_,_,[e1;e2])  when is_sbv(e1.Type) -> Some (fun c -> z3.MkBvSge(c.mke e1, c.mke e2))
           | LessQ(_,_,[e1;e2])       when is_sbv(e1.Type) -> Some (fun c -> z3.MkBvSlt(c.mke e1, c.mke e2))
           | LessEqQ(_,_,[e1;e2])     when is_sbv(e1.Type) -> Some (fun c -> z3.MkBvSle(c.mke e1, c.mke e2))
           | BitwiseAndQ(_,_,[e1;e2]) when is_bv(e1.Type)  -> Some (fun c -> z3.MkBvAnd(c.mke e1, c.mke e2))
           | BitwiseOrQ(_,_,[e1;e2])  when is_bv(e1.Type)  -> Some (fun c -> z3.MkBvOr(c.mke e1, c.mke e2))
           | BitwiseXorQ(_,_,[e1;e2]) when is_bv(e1.Type)  -> Some (fun c -> z3.MkBvXor(c.mke e1, c.mke e2))
           | BitwiseNotQ(_,_,[e1])    when is_bv(e1.Type)  -> Some (fun c -> z3.MkBvNeg(c.mke e1))
           | ShiftLeftQ(_,_,[e1;e2])  when is_bv(e1.Type)  -> Some (fun c -> z3.MkBvShl(c.mke e1, c.mke e2))
           | ShiftRightQ(_,_,[e1;e2]) when is_ubv(e1.Type) -> Some (fun c -> z3.MkBvLshr(c.mke e1, c.mke e2)) // TBD check types
           | ShiftRightQ(_,_,[e1;e2]) when is_sbv(e1.Type) -> Some (fun c -> z3.MkBvAshr(c.mke e1, c.mke e2))
           // signs of bit-vectors will have to be re-constructed by operands.
           | DerivedPatterns.SByte(b)    -> Some (fun _ -> z3.MkNumeral((int32)b, z3.MkBvSort(8u)))
           | DerivedPatterns.Int16(i16)  -> Some (fun _ -> z3.MkNumeral((int32)i16, z3.MkBvSort(16u)))
           | DerivedPatterns.Int32(i32)  -> Some (fun _ -> z3.MkNumeral(i32, z3.MkBvSort(32u)))
           | DerivedPatterns.Int64(i64)  -> Some (fun _ -> z3.MkNumeral(i64, z3.MkBvSort(64u)))
           | DerivedPatterns.Byte(b)     -> Some (fun _ -> z3.MkNumeral((int32)b, z3.MkBvSort(8u)))
           | DerivedPatterns.UInt16(i16) -> Some (fun _ -> z3.MkNumeral((int32)i16, z3.MkBvSort(16u)))
           | DerivedPatterns.UInt32(i32) -> Some (fun _ -> z3.MkNumeral(i32, z3.MkBvSort(32u)))
           | DerivedPatterns.UInt64(i64) -> Some (fun _ -> z3.MkNumeral(i64, z3.MkBvSort(64u)))
           | ConvSByteQ(_,_,[e1])  when is_bv(e1.Type)  -> convert2signed 8ul e1 
           | ConvInt16Q(_,_,[e1])  when is_bv(e1.Type)  -> convert2signed 16ul e1 
           | ConvInt32Q(_,_,[e1])  when is_bv(e1.Type)  -> convert2signed 32ul e1            
           | ConvInt64Q(_,_,[e1])  when is_bv(e1.Type)  -> convert2signed 64ul e1          
           | ConvByteQ(_,_,[e1])   when is_bv(e1.Type)  -> convert2unsigned 8ul e1  
           | ConvUInt16Q(_,_,[e1]) when is_bv(e1.Type)  -> convert2unsigned 16ul e1  
           | ConvUInt32Q(_,_,[e1]) when is_bv(e1.Type)  -> convert2unsigned 32ul e1           
           | ConvUInt64Q(_,_,[e1]) when is_bv(e1.Type)  -> convert2unsigned 64ul e1                                   
           | MinQ(_,_,[e1;e2])     when is_ubv(e1.Type) -> mk_cmp z3.MkBvUlt e1 e2
           | MaxQ(_,_,[e1;e2])     when is_ubv(e1.Type) -> mk_cmp z3.MkBvUgt e1 e2
           | MinQ(_,_,[e1;e2])     when is_sbv(e1.Type) -> mk_cmp z3.MkBvSlt e1 e2 
           | MaxQ(_,_,[e1;e2])     when is_sbv(e1.Type) -> mk_cmp z3.MkBvSgt e1 e2  
           | _ -> None
       override this.mk_type ty = 
           match ty with
           | _ when ty = typeof<int8>    -> Some(fun _ -> z3.MkBvSort(8ul))
           | _ when ty = typeof<int16>   -> Some(fun _ -> z3.MkBvSort(16ul))
           | _ when ty = typeof<int32>   -> Some(fun _ -> z3.MkBvSort(32ul))
           | _ when ty = typeof<int64>   -> Some(fun _ -> z3.MkBvSort(64ul))
           | _ when ty = typeof<uint8>   -> Some(fun _ -> z3.MkBvSort(8ul))
           | _ when ty = typeof<uint16>  -> Some(fun _ -> z3.MkBvSort(16ul))
           | _ when ty = typeof<uint32>  -> Some(fun _ -> z3.MkBvSort(32ul))
           | _ when ty = typeof<uint64>  -> Some(fun _ -> z3.MkBvSort(64ul))
           | _ -> None
    end
  
  ///
  /// Plugin for strings.
  ///
  type StringPlugin(z3:Context) = class
       inherit IPlugin()
       let string_sort = z3.MkSort("string")
       let char_sort   = z3.MkSort("char")
       
       override this.mk_expr e = 
         match e with
         // TBD, these are distinct constants.
         | DerivedPatterns.Char(c)   -> Some (fun c -> z3.MkConst(c.ToString(), char_sort))
         | DerivedPatterns.String(s) -> Some (fun c -> z3.MkConst(s, string_sort))
         | _ -> None
       override this.mk_type ty = 
         match ty with
          | _ when ty = typeof<string> -> Some (fun _ -> string_sort)
          | _ when ty = typeof<char>   -> Some (fun _ -> char_sort)
          | _ -> None
     end

  ///
  /// Plugin for Boolean operators.
  ///
     
  type BoolPlugin(z3:Context) = class
       inherit IPlugin()
       let bool_sort = z3.MkBoolSort()
       let true_term = z3.MkTrue()
       let false_term = z3.MkFalse()
       let (|OrQ|_|)    = (|SpecificCall|_|) <@ ( || ) @>
       let (|AndQ2|_|)    = (|SpecificCall|_|) <@ ( && ) @>
       override this.mk_expr e = 
         match e with
         | DerivedPatterns.AndAlso(x1,x2) -> Some (fun c -> z3.MkAnd(c.mke x1, c.mke x2))
         | DerivedPatterns.OrElse(x1,x2)  -> Some (fun c -> z3.MkOr(c.mke x1, c.mke x2))
         | NotQ(_,_,[e1]) ->                   Some (fun c -> z3.MkNot(c.mke e1))
         | DerivedPatterns.Bool(true)   ->   Some (fun c -> true_term)
         | DerivedPatterns.Bool(false)  ->   Some (fun c -> false_term)
         | OrQ(_,_,[x1;x2])            ->   Some (fun c -> z3.MkOr(c.mke x1, c.mke x2))
         | AndQ2(_,_,[x1;x2])           ->   Some (fun c -> z3.MkAnd(c.mke x1, c.mke x2))
         | _ -> None
         
       override this.mk_type ty = 
          if ty = typeof<bool> then Some (fun _ -> bool_sort) else None
     end

  ///
  /// Plugin for basic Logic services
  ///     
  type LogicPlugin(z3:Context) = class
    inherit IPlugin()
    let (|ForallQ|_|)  = (|SpecificCall|_|) <@ Logic.forall @>
    let (|ExistsQ|_|)  = (|SpecificCall|_|) <@ Logic.exists @>
    let (|ForallPQ|_|) = (|SpecificCall|_|) <@ Logic.forall_p @>
    let (|ExistsPQ|_|) = (|SpecificCall|_|) <@ Logic.exists_p @>
    let (|BigAndQ|_|)  = (|SpecificCall|_|) <@ Logic.big_and @>
    let (|IteQ|_|)     = (|SpecificCall|_|) <@ Logic.ite @>
    let (|EqQ|_|)      = (|SpecificCall|_|) <@ Logic.equals @>
    let (|DeclareVarsQ|_|) = (|SpecificCall|_|) <@ Logic.declare @>     
    let (|DeclarePredsQ|_|) = (|SpecificCall|_|) <@ Logic.declare_preds @> 
        
    override this.mk_type ty = None
    
    override this.mk_expr e = 
        let mk_q varss body c =
            let v_list = ref []
            let add_var env (var:Var) = 
                let v = z3.MkFreshConst(var.Name, c.mkt var.Type)
                v_list := v::(!v_list)
                c.add_env env var v
            let add_vars env vars = List.fold add_var env vars
            let add_varss env varss = List.fold add_vars env varss
            let env = add_varss c.env varss
            let body = c.mke_env env body
            let bound_vars = Array.ofList (!v_list)
            (bound_vars, body)         
        let mk_q2 varss pattern varss2 body c =
            let v_list = ref []
            let add_var env (var:Var) = 
                let v = z3.MkFreshConst(var.Name, c.mkt var.Type)
                v_list := v::(!v_list)
                c.add_env env var v
            let vars = List.concat varss
            let env1 = List.fold add_var c.env vars
            let pattern = c.mke_env env1 pattern
            let vars2 = List.concat varss2
            let v_list = List.rev (!v_list)
            // checking types is TBD
            let add_var2 env v (var:Var) = c.add_env env var v
            let env2 = List.fold2 add_var2 c.env v_list vars2
            let body = c.mke_env env2 body
            let bound_vars = Array.ofList v_list
            (bound_vars, pattern, body)         
        match e with
         | ForallQ(_,_,[DerivedPatterns.Lambdas(varss, body)]) -> 
           Some (fun c -> 
                 let (bound_vars, body) = mk_q varss body c
                 z3.MkForall(0ul, bound_vars, [||], body)           
                )
         | ExistsQ(_,_,[DerivedPatterns.Lambdas(varss, body)]) -> 
           Some (fun c -> 
                 let (bound_vars, body) = mk_q varss body c
                 z3.MkExists(0ul, bound_vars, [||], body)           
                )                 
         | ForallPQ(_,_,[DerivedPatterns.Lambdas(x, p);DerivedPatterns.Lambdas(var, body)]) -> 
           Some (fun c ->
                 let (bound_vars, pat, body) = mk_q2 x p var body c
                 let p = z3.MkPattern [| pat |]
                 z3.MkForall(0ul, bound_vars, [| p |], body))    
         | ExistsPQ(_,_,[DerivedPatterns.Lambdas(x, p);DerivedPatterns.Lambdas(var, body)]) -> 
           Some (fun c ->
                 let (bound_vars, pat, body) = mk_q2 x p var body c
                 let p = z3.MkPattern [| pat |]
                 z3.MkExists(0ul, bound_vars, [| p |], body))   
         | BigAndQ(_,_, [DerivedPatterns.Int32(n); ExprShape.ShapeLambda(x, p)]) -> 
           Some (fun c -> 
                 let exprs = ref [] 
                 for i = 0 to n-1 do
                     let m = z3.MkNumeral(i,z3.MkBvSort(32ul))
                     let env = c.add_env c.env x m
                     let e = c.mke_env env p
                     exprs := e::(!exprs) 
                 done
                 let exprs = Array.ofList (!exprs)
                 z3.MkAnd(exprs))
         | IteQ(_,_,[e1;e2;e3]) -> 
           Some (fun c -> 
                 let e1 = c.mke e1
                 let e2 = c.mke e2
                 let e3 = c.mke e3
                 z3.MkIte(e1,e2,e3))           
         | EqQ(_,_,[e1;e2]) -> 
           Some (fun c -> z3.MkEq(c.mke e1, c.mke e2))
         | Patterns.Application(EqQ(_,_,[e1]), e2) -> 
           Some (fun c -> z3.MkEq(c.mke e1, c.mke e2))              
         | Patterns.Application(Patterns.Application(EqQ(_,_,[]), e1), e2) -> 
           Some (fun c -> z3.MkEq(c.mke e1, c.mke e2))            
         | DeclareVarsQ(_,_,[ExprShape.ShapeLambda _ as e1]) ->
           Some (fun c ->  
                 let rec declare_rec env e = 
                     match e with 
                      | ExprShape.ShapeLambda(var,e) -> 
                        let v = z3.MkConst(var.Name, c.mkt var.Type)
                        declare_rec (c.add_env env var v) e
                      | _ -> c.mke_env env e
                 declare_rec c.env e1)
         | _ -> None
    end

  ///
  /// Plugin for the unit type.
  ///
  type UnitPlugin(z3:Context) = class
       inherit IPlugin()
       let mk_unit = ref (null : FuncDecl) 
       let unit_sort = z3.MkTupleSort("unit", [||], [||], mk_unit, [||])
       let mk_unit = z3.MkConst(!mk_unit)
       
       override this.mk_expr e = 
         match e with
         | DerivedPatterns.Unit -> Some (fun c -> mk_unit)
         | _ -> None
         
       override this.mk_type ty = 
         if ty = typeof<unit> then Some (fun _ -> unit_sort) else None

     end    
     
  type FloatPlugin(z3:Context) = class
       inherit IPlugin()
       let single_sort = z3.MkSort("single")
       let double_sort   = z3.MkSort("double")
       override this.mk_expr e = 
         match e with
         // TBD, these are distinct constants.
         | DerivedPatterns.Single(s) -> Some (fun c -> z3.MkConst(s.ToString(), single_sort))
         | DerivedPatterns.Double(d) -> Some (fun c -> z3.MkConst(d.ToString(), double_sort))
         | _ -> None
       override this.mk_type ty = 
         match ty with
          | _ when ty = typeof<single> -> Some (fun _ -> single_sort)
          | _ when ty = typeof<double>   -> Some (fun _ -> double_sort)
          | _ -> None           
    end
    

  ///
  /// plugin for combinatory array logic.
  ///
  type ArrayPlugin(z3:Context) = class
       inherit IPlugin()
       let (|K|_|)      = (|SpecificCall|_|) <@ Arrays.K  @>
       let (|I|_|)    = (|SpecificCall|_|) <@ Arrays.I  @>
       let (|Store|_|)  = (|SpecificCall|_|) <@ Arrays.store  @>
       let (|Select|_|) = (|SpecificCall|_|) <@ Arrays.select  @>
       let (|Map1|_|)   = (|SpecificCall|_|) <@ Arrays.map1  @>
       let (|Map2|_|)   = (|SpecificCall|_|) <@ Arrays.map2  @>
       let (|Map3|_|)   = (|SpecificCall|_|) <@ Arrays.map3  @>
       let (|DefaultQ|_|) = (|SpecificCall|_|) <@ Arrays.default_value  @>
       let func_decl_cache = System.Collections.Generic.Dictionary<_,_>()
       let id_cache = System.Collections.Generic.Dictionary<_,_>()
             
       let range ty =
           assert (FSharpType.IsFunction ty)
           snd(FSharpType.GetFunctionElements(ty))

       let domain ty = 
           assert (FSharpType.IsFunction ty)
           fst(FSharpType.GetFunctionElements(ty))
                            
           
       let rec get_function_type ty n dom =
           if n = 0 then (List.rev dom,ty) else
           let (d,r) = FSharpType.GetFunctionElements(ty)
           get_function_type r (n-1) (d::dom)
       let rec get_function_name (e:Expr) = 
           match e with
           | DerivedPatterns.Applications(f,_) -> get_function_name f
           | DerivedPatterns.Lambdas(_,body) -> get_function_name body
           | Patterns.Call(_,mi,_) -> mi.Name
           | _ -> e.ToString()
       let rec mk_apps f args = 
           match f, args with
           | _, [] -> f
           | Patterns.Lambda(x,e), arg::args -> 
             let f = e.Substitute (fun y -> if x = y then Some arg else None)
             mk_apps f args
           | _, arg::args -> mk_apps (Expr.Application(f,arg)) args
           
       // TODO: does not work if 'f' is map
       let is_map f = 
           match f with
           | DerivedPatterns.Lambdas(_, Map1(_,_,_)) -> true
           | DerivedPatterns.Lambdas(_, Map2(_,_,_)) -> true
           | DerivedPatterns.Lambdas(_, Map3(_,_,_)) -> true
           | _ -> false           
           
       let lookup_cache (f:Expr) = 
           match f with
           | DerivedPatterns.Lambdas(_, Patterns.Call(_,mi,_)) -> 
             match func_decl_cache.TryGetValue(mi) with   
             | true, v -> Some v
             | false, _ -> None
           | _ -> None
           
       let insert_cache  f f' =
           match f with
           | DerivedPatterns.Lambdas(_, Patterns.Call(_,mi,_)) -> 
             func_decl_cache.Add(mi, f')  
           | _ -> ()
           

       let rec get_decl c f num_args =  
           match f with
           | DerivedPatterns.Lambdas(_, Map1(_,_,[f1;e1])) ->
             let f2 = get_decl c f1 1
             let t1 = z3.MkBound(0ul, c.mkt e1.Type)
             z3.MkArrayMap(f2, [| t1 |]).GetAppDecl()
           | DerivedPatterns.Lambdas(_, Map2(_,_,[f1;e1;e2])) ->
             let f2 = get_decl c f1 2
             let t1 = z3.MkBound(0ul, c.mkt e1.Type)
             let t2 = z3.MkBound(1ul, c.mkt e2.Type)
             let t = z3.MkArrayMap(f2, [| t1; t2 |])
             t.GetAppDecl()
           | DerivedPatterns.Lambdas(_, Map3(_,_,[f1;e1;e2;e3])) ->
             let f2 = get_decl c f1 3
             let t1 = z3.MkBound(0ul, c.mkt e1.Type)
             let t2 = z3.MkBound(1ul, c.mkt e2.Type)
             let t3 = z3.MkBound(2ul, c.mkt e3.Type)             
             z3.MkArrayMap(f2, [| t1; t2; t3 |]).GetAppDecl()
           | DerivedPatterns.Lambdas(_, Select(_,_,[e1;e2])) ->
             let t1 = z3.MkBound(0ul, c.mkt e1.Type)
             let t2 = z3.MkBound(1ul, c.mkt e2.Type)           
             z3.MkArraySelect(t1,t2).GetAppDecl()     
           | DerivedPatterns.Lambdas(_, (K(_,_,[e1]) as e)) -> 
             let dom = domain e.Type
             let t1 = z3.MkBound(0ul, c.mkt e1.Type)        
             z3.MkArrayConst(c.mkt dom, t1).GetAppDecl()                 
           | _ -> 
             match lookup_cache f with
             | Some v -> v
             | None ->
             let dom, range = get_function_type f.Type num_args []
             let dom' = List.map c.mkt dom
             let range' = c.mkt range
             let f'    = z3.MkFreshFuncDecl(get_function_name f, Array.ofList dom', range')
             let xs    = List.map (fun d -> z3.MkFreshConst("x",d)) dom'
             let vs    = List.map2 (fun x d -> Var(x.ToString(), d)) xs dom
             let ves   = List.map Expr.Var vs 
             // 
             // use the empty environment. 
             // this forces only accepting closed expressions f.
             // 
             let env   = List.fold2 c.add_env (c.env0()) vs xs
             let e_p    = mk_apps f ves
             // printf "f': %O\ne': %O\n" f' e_p
             let fbody = c.mke_env env e_p
             let farg  = z3.MkApp(f',Array.ofList xs)
             let fdef  = z3.MkForall(0ul, Array.ofList xs, [|z3.MkPattern [|farg|] |], z3.MkEq(farg,fbody))
             if !Debug.debug then Printf.printf "Asserting: %O\n" fdef;
             z3.AssertCnstr(fdef);
             insert_cache f f';
             f'
                                 
       let mk_aux_function c (f:Expr) args = 
           let args1 = Array.map c.mke args
           let f' = get_decl c f (Array.length args1)       
           z3.MkArrayMap(f', args1)     
                

                  
       // convert lambda expressions into combinators.
       let free_vars = System.Collections.Generic.Dictionary<_,_>()
       let rec get_free_vars e = 
           match free_vars.TryGetValue(e) with
           | true, v -> v
           | false, _ -> 
             let s = get_free_vars_mem e
             free_vars.Add(e,s);
             s
       and get_free_vars_mem e =            
           match e with
           | ExprShape.ShapeVar y -> Set.singleton(y)           
           | ExprShape.ShapeCombination(_,args) -> 
             List.fold (fun s a -> Set.union s (get_free_vars a)) Set.empty args
           | ExprShape.ShapeLambda(y,body) -> Set.remove y (get_free_vars body)
           
       let mk_k (e:Expr) ty = 
           apply_generic <@@ Arrays.K @@> [| e.Type; ty |]  [e]
           // Printf.printf "mk_k %O\nresult:%O\nindex: %s\n" e res (ty.ToString())
           // res
           
       let mk_i ty = 
           let x = Var("x",ty)
           Expr.Lambda(x,Expr.Var x)

       
       // 
       // convert lambda term into CAL term using
       // map, K, I
       // 
       
       let mk_map1_term (f:Expr) (e1:Expr) =
           let (d1,r1) = FSharpType.GetFunctionElements(f.Type)
           let (d2,r2) = FSharpType.GetFunctionElements(e1.Type)
           assert (r2 = d1)
           apply_generic <@@ Arrays.map1 @@> [|d1;r1;d2|] [f; e1]       
           // we wish it was so simple:
           // <@@ Arrays.map1 (%%f) (%%e1) @@>   

       let mk_map2_term (f:Expr) (e1:Expr) (e2:Expr) =
           let (d1,r1) = FSharpType.GetFunctionElements(f.Type)
           let (d1',r1') = FSharpType.GetFunctionElements(r1)
           let (d2,r2) = FSharpType.GetFunctionElements(e1.Type)
           let (d3,r3) = FSharpType.GetFunctionElements(e2.Type)
           assert (r2 = d1)
           assert (r3 = d1')
           assert (d2 = d3)
           apply_generic <@@ Arrays.map2 @@> [|d1;d1';r1';d2|] [f; e1; e2]      
           // we wish it was so simple:
           // <@@ Arrays.map2 (%%f) (%%e1) (%%e2) @@>               

       let mk_map3_term (f:Expr) (e1:Expr) (e2:Expr) (e3:Expr) =
           let (d1,r1) = FSharpType.GetFunctionElements(f.Type)
           let (d1',r1') = FSharpType.GetFunctionElements(r1)
           let (d1'',r1'') = FSharpType.GetFunctionElements(r1')
           let (d2,r2) = FSharpType.GetFunctionElements(e1.Type)
           let (d3,r3) = FSharpType.GetFunctionElements(e2.Type)
           let (d4,r4) = FSharpType.GetFunctionElements(e3.Type)
           assert (r2 = d1)
           assert (r3 = d1')
           assert (r4 = d1'')
           assert (d2 = d3)
           assert (d2 = d4)
           apply_generic <@@ Arrays.map3 @@> [|d1;d1';d1'';r1'';d2|] [f; e1; e2; e3]    
           // we wish it was so simple:
           // <@@ Arrays.map3 (%%f) (%%e1) (%%e2) (%%e3) @@>  
           
       let rec mk_map1 (x:Var) mi (e1:Expr) =
           let v = Var("v1",e1.Type)
           let f = Expr.Lambda(v, mk_call mi [Expr.Var v])
           mk_map1_term f (lift x e1)
       and mk_map2 (x:Var) mi (e1:Expr) (e2:Expr) = 
           let v = Var("v2",e1.Type)
           let w = Var("w2",e2.Type)
           let f = Expr.Lambda(v, Expr.Lambda(w, mk_call mi [Expr.Var v;Expr.Var w]))
           mk_map2_term f (lift x e1) (lift x e2)    
       and mk_map3 (x:Var) mi (e1:Expr) (e2:Expr) (e3:Expr) =     
           let v = Var("v3",e1.Type)
           let w = Var("w3",e2.Type)
           let u = Var("u3",e3.Type)
           let f = Expr.Lambda(v, Expr.Lambda(w, Expr.Lambda(u, mk_call mi [Expr.Var v;Expr.Var w;Expr.Var u])))
           mk_map3_term f (lift x e1) (lift x e2) (lift x e3)
                   
       and lift (x:Var) (e:Expr) = 
           let (res:Expr) = lift_rec x e
           assert (let (d,r) = FSharpType.GetFunctionElements(res.Type) in (x.Type = d && r = e.Type))
           res
       and lift_rec x e =            
           match e with
           | _ when not (Set.contains x (get_free_vars e)) -> mk_k e x.Type
           | ExprShape.ShapeVar y when x.Equals(y) -> mk_i x.Type
           | ExprShape.ShapeVar _  -> mk_k e x.Type
           | ExprShape.ShapeLambda(y, body) -> lift x (lift y body)
           | Patterns.IfThenElse(e1,e2,e3) ->
             let ty = e2.Type 
             let e1 = lift x e1
             let e2 = lift x e2
             let e3 = lift x e3
             let ite = mk_typed_fun_call <@@ Logic.ite @@> [|e2.Type|]
             // we wish it was so simple:
             // <@@ Arrays.map3 (%%ite) (%%e1) (%%e2) (%%e3) @@>
             apply_generic <@@ Arrays.map3 @@> [| typeof<bool>; ty; ty; ty; x.Type|] [ite; e1; e2; e3]
           | Map1(_,_,[f;e1]) -> 
             let y =  Var("y",e1.Type)             
             let mapf = Expr.Lambda(y, mk_map1_term f (Expr.Var y))
             mk_map1_term mapf (lift x e1)
           | Map2(_,_,[f;e1;e2]) -> 
             let y =  Var("y",e1.Type)             
             let z =  Var("z",e2.Type)
             let mapf = Expr.Lambda(y,Expr.Lambda(z, mk_map2_term f (Expr.Var y) (Expr.Var z)))
             mk_map2_term mapf (lift x e1) (lift x e2)            
           | Map3(_,_,[f;e1;e2;e3]) -> 
             let y =  Var("y",e1.Type)             
             let z =  Var("z",e2.Type)
             let u =  Var("u",e3.Type)             
             let mapf = Expr.Lambda(y,
                          Expr.Lambda(z, 
                            Expr.Lambda(u, 
                              mk_map3_term f (Expr.Var y) (Expr.Var z) (Expr.Var u))))   
             mk_map3_term mapf (lift x e1) (lift x e2) (lift x e3)                            
           | Patterns.Call(None,mi,[e1]) -> mk_map1 x mi e1
           | Patterns.Call(None,mi,[e1;e2]) -> mk_map2 x mi e1 e2             
           | Patterns.Call(None,mi,[e1;e2;e3]) -> mk_map3 x mi e1 e2 e3
           | Patterns.Application(e1,e2) ->
             let ty_e1 = e1.Type
             let ty_e2 = e2.Type
             let ty_e  = e.Type
             let xe1 = lift x e1
             let xe2 = lift x e2
             let sel = mk_typed_fun_call <@@ Arrays.select @@> [| ty_e2; ty_e |]
             // we wish it was so simple:
             // <@@ Arrays.map2 Arrays.select (%%e1) (%%e2) @@>    
             // Printf.printf "lifing %O\n%O\n%O\n" sel xe1 xe2
             apply_generic <@@ Arrays.map2 @@> [| ty_e1; ty_e2; ty_e; x.Type |] [ sel; xe1; xe2]         
           | ExprShape.ShapeCombination(f,args) -> 
             raise (System.Exception (Printf.sprintf "Cannot lift %O\n" e))

                     
       let mk_id (x:Var) = 
            Some (fun c -> 
               match id_cache.TryGetValue(x.Type) with
               | true, v -> v
               | false, _ -> 
                 let t = c.mkt x.Type
                 let ty = z3.MkArraySort(t, t)
                 let id = z3.MkConst("Id", ty)
                 let bx = z3.MkBound(0ul, t)
                 let idx = z3.MkArraySelect(id,bx)
                 let pats = [| z3.MkPattern [| idx |] |]
                 let eq = z3.MkEq(idx, bx)
                 z3.AssertCnstr(z3.MkForall(0ul, pats, [| t |], [| "x" |],  eq))
                 id_cache.Add(x.Type,id)
                 id
               )
               
       override this.mk_expr e = 
          match e with
          | K(_,_,[e1]) -> Some (fun c -> z3.MkArrayConst(c.mkt (domain e.Type), c.mke e1))
          | Store(_,_,[e1;e2;e3])  -> Some (fun c -> z3.MkArrayStore(c.mke e1, c.mke e2, c.mke e3))
          | Select(_,_,[e1;e2])    -> Some (fun c -> z3.MkArraySelect(c.mke e1, c.mke e2))
          | Map1(_,_,[f;e1])       -> Some (fun c -> mk_aux_function c f [|e1|])
          | Map1(_,_,_)            -> Printf.printf "not recognized %O\n" e; None
          | Map2(_,_,[f;e1;e2])    -> Some (fun c -> mk_aux_function c f [|e1;e2|]) 
          | Map2(_,_,_)            -> Printf.printf "not recognized %O\n" e; None      
          | Map3(_,_,[f;e1;e2;e3]) -> Some (fun c -> mk_aux_function c f [|e1;e2;e3|]) 
          | Map3(_,_,_)            -> Printf.printf "not recognized %O\n" e; None 
          | DefaultQ(_,_,[e1])     -> Some (fun c -> z3.MkArrayDefault(c.mke e1))
          | Patterns.Lambda(x,Patterns.Var y) when x = y -> mk_id x
          | Patterns.Lambda(x, I(_,_,[y])) when Expr.Var x = y -> mk_id x
          | _ -> None

       override this.mk_type ty = None
       
       member this.mk_lift x e = lift x e
    end
    
  ///
  /// Place-holder plugin for the contract library.
  ///
  type ContractPlugin(z3:Context) = class
       inherit IPlugin()
//     let (|ForallQ|_|) =  (|SpecificCall|_|) <@ System.Diagnostics.Contracts.Contract.ForAll @>  

       override this.mk_expr e = 
         match e with
         (*
         | ForallQ(s,pred) -> Some(...)
         | ExistsQ(s,pred) -> Some(...)
         *)
         | _ -> None
         
       override this.mk_type ty = None
    end    
      
  let memoize (d : System.Collections.Generic.Dictionary<'a,'b>) v cl = 
      match d.TryGetValue(v) with
         | true, vl -> vl
         | false, _ -> 
           let vl = cl v
           if not (d.ContainsKey(v)) then d.Add(v,vl);
           vl
           
  exception NotPredefined

  ///
  /// The main translator class.
  /// It contains a main function that translates
  /// quoted expressions to Z3 terms.
  /// It dispatches on plugins to handle special 
  /// cases.
  /// 
      
  type Translator(z3:Context) = class
     let (&&&) x y = z3.MkAnd(x,y)
     let (===) x y = z3.MkEq(x,y)
     let array_plugin = ArrayPlugin(z3)
     let mutable plugins = [
               (NumPlugin(z3)    :> IPlugin); 
               (BvPlugin(z3)     :> IPlugin);
               (StringPlugin(z3) :> IPlugin);
               //(FloatPlugin(z3)  :> IPlugin);
               (UnitPlugin(z3)   :> IPlugin);
               (BoolPlugin(z3)   :> IPlugin);
               (array_plugin     :> IPlugin);
               (LogicPlugin(z3)  :> IPlugin)
               ]
     
     let showAll = System.Reflection.BindingFlags.Public ||| System.Reflection.BindingFlags.NonPublic

     let rec find_plugin_ty ty plugins = 
         match plugins with
         | [] -> None
         | (p:IPlugin)::plugins -> 
           match p.mk_type ty with
           | None -> find_plugin_ty ty plugins
           | s -> s

     let rec find_plugin_expr e plugins = 
         match plugins with
         | [] -> None
         | (p:IPlugin)::plugins -> 
           match p.mk_expr e with
           | None -> find_plugin_expr e plugins
           | s -> s

     let (|PluginType|_|) ty = find_plugin_ty ty plugins         
     let (|PluginQ|_|) e = find_plugin_expr e plugins
     
     let is_enumeration_type uinfos = 
         Array.forall (fun (u:UnionCaseInfo) -> u.GetFields().Length = 0) uinfos

     let mk_enumeration_type ty uinfos = 
         let names = Array.map (fun (u:UnionCaseInfo) -> u.Name) uinfos in
         let name = ty.ToString() in
         let n = names.Length in
         let decls = Array.init n (fun i -> null) in
         let testers = Array.init n (fun i -> null) in
         let s = z3.MkEnumerationSort(name, names, decls, testers) in
         (s, List.init n (fun i -> (uinfos.[i], decls.[i], testers.[i], [||])))
     
     let union_types = System.Collections.Generic.Dictionary<System.Type,_>()
     let tuple_types = System.Collections.Generic.Dictionary<System.Type,_>()
     let record_types = System.Collections.Generic.Dictionary<System.Type,_>()
     let obj_sort = z3.MkSort("obj")
     
     let is_predefined_type ty = 
         union_types.ContainsKey(ty) ||
         tuple_types.ContainsKey(ty) ||
         record_types.ContainsKey(ty) ||
         ty = typeof<obj> || 
         match ty with
         | PluginType _ -> true
         | _ -> false

     let rec get_predefined_type ty = 
         match ty with
         | PluginType mkt ->            
           let get_pre ty = match get_predefined_type ty with None -> raise NotPredefined | Some t -> t
           try 
              Some (mkt get_pre)
           with
             | NotPredefined -> None
         | _ -> 
         match union_types.TryGetValue(ty) with
         | true, (s,_) -> Some s
         | false, _ ->
         match tuple_types.TryGetValue(ty) with
         | true, (s,_,_) -> Some s
         | false, _ ->
         match record_types.TryGetValue(ty) with
         | true, (s,_,_,_) -> Some s
         | false, _ -> if typeof<obj> = ty then Some obj_sort else None
         

         
     // recognize basic recursive types.
     // A type is a basic (mutually) recursive type
     // if it can be written as a sequence of mutually recursive
     // definitions. Each constructor is allowed to take a list
     // of arguments. The arguments must have types that have either
     // been already declared or have types that are currently being
     // declared.
     // An example recursive type that is not supported by Z3 is:
     // type t1 = { t2 : t2; x : int }
     // and  t2 = | T1 of t1 | T2 of t2 | T3 of int 
     // It is not supported since, the type t1 is a record type.
     // Also not supported is:
     // type 'a t1 = { t2 : 'a; x : int }
     // and  t2 = | T1 of t1<t2> | I of int 
     // or even:
     // type opt_list = | cons of int option * opt_list | nil
     // (the option type is not declared simultaneously with opt_list).
     //
     let mk_basic_data_type ty = 
         assert (FSharpType.IsUnion ty);
         let data_types = System.Collections.Generic.Dictionary<System.Type,_>()
         let uinfos = FSharpType.GetUnionCases (ty,showAll)
         let idx = ref 1
         data_types.Add(ty,(uinfos,0))
         let rec is_basic_uis uinfos = Array.forall is_basic_ui uinfos
         and is_basic_ui (u:UnionCaseInfo) = Array.forall is_basic_field (u.GetFields())
         and is_basic_field (f:System.Reflection.PropertyInfo) = 
             let ty = f.PropertyType 
             is_predefined_type ty ||
             data_types.ContainsKey(ty) ||
             (FSharpType.IsUnion ty && 
              let uinfos = FSharpType.GetUnionCases(ty, showAll)
              data_types.Add(ty, (uinfos,!idx));
              idx := 1 + !idx;
              is_basic_uis uinfos)
         if not (is_basic_uis uinfos) then 
            None
         else 
            // all fields are basic. 
            // now build the data-type
            let num_types = !idx
            let names = Array.init num_types (fun i -> null)
            let constrs = Array.init num_types (fun i -> null)
            let mk_field_name (f:System.Reflection.PropertyInfo) = f.Name
            let mk_field_sort (f:System.Reflection.PropertyInfo) = 
                let ty = f.PropertyType
                match get_predefined_type ty with
                | Some t -> t
                | None -> null
            let mk_field_ref (f:System.Reflection.PropertyInfo) = 
                let ty = f.PropertyType
                match data_types.TryGetValue(f.PropertyType) with
                | true, (_,idx) -> (uint32)idx
                | false, _ -> 0ul                            
            let mk_constr (u:UnionCaseInfo) = 
                let fields = u.GetFields()
                z3.MkConstructor(u.Name, Printf.sprintf "Test%s" u.Name,
                                Array.map mk_field_name fields,
                                Array.map mk_field_sort fields,
                                Array.map mk_field_ref  fields)
            for kv in data_types do
                let ty = kv.Key
                let (uinfos, n) = kv.Value
                names.[n] <- ty.Name
                constrs.[n] <- Array.map mk_constr uinfos 
            done
            let sorts = z3.MkDataTypes(names, constrs)
            for kv in data_types do
                let ty = kv.Key
                let (uinfos, n) = kv.Value
                let cnstr_list = constrs.[n] 
                let s = sorts.[n]
                let vl = List.init uinfos.Length 
                             (fun i -> (uinfos.[i], 
                                        z3.GetConstructor(cnstr_list.[i]),
                                        z3.GetTester(cnstr_list.[i]),
                                        z3.GetAccessors(cnstr_list.[i])))
                union_types.Add(kv.Key, (s, vl))
            done
            // Dispose of the constructor
            for constr_list in constrs do
                for constr in constr_list do
                    constr.Dispose()
                done
            done
            Some (fun _ -> sorts.[0])
                     
     let mk_tuple_type sorts = 
         let n = Array.length sorts in
         let mk_tup = ref (null : FuncDecl) in
         let projs = Array.init n (fun _ -> null) in
         let names = Array.init n (fun i -> Printf.sprintf "proj_%d" i) in
         let s = z3.MkTupleSort("mk_tuple", names, sorts, mk_tup, projs) in
         (s, !mk_tup, projs)         
     
     let rec mk_union_type ty = memoize union_types ty mk_union_type_mem
     and mk_union_type_mem ty = 
         let uinfos = FSharpType.GetUnionCases (ty,showAll) in
         if is_enumeration_type uinfos then
            mk_enumeration_type ty uinfos 
         else 
            match mk_basic_data_type ty with
            | None -> raise (System.Exception "No support for generic data-types")
            | Some s -> union_types.[ty]
              
           
     and mk_tuple_type1 ty = memoize tuple_types ty mk_tuple_type_mem     
     and mk_tuple_type_mem ty = 
         let types = FSharpType.GetTupleElements ty in
         let sorts = Array.map mk_type types in
         mk_tuple_type sorts

     and mk_record_type ty = memoize record_types ty mk_record_type_mem
     and mk_record_type_mem ty = 
         let fields = FSharpType.GetRecordFields ty in
         let n = Array.length fields in
         let mk_rec = ref (null : FuncDecl) in
         let projs = Array.init n (fun _ -> null) in
         let names = Array.map (fun (pi:System.Reflection.PropertyInfo) -> pi.Name) fields in
         let sorts = Array.map (fun (pi:System.Reflection.PropertyInfo) -> mk_type pi.PropertyType) fields in
         let s = z3.MkTupleSort("mk_record", names, sorts, mk_rec, projs) in
         (s, !mk_rec, fields, projs)
                              
     and mk_type ty = 
         match ty with
         | PluginType result -> result mk_type
         | _ when FSharpType.IsUnion(ty) -> 
           let (s, _) = mk_union_type ty in
           s
         | _ when FSharpType.IsTuple ty -> 
           let (s, _, _) = mk_tuple_type1 ty in
           s
         | _ when FSharpType.IsRecord ty ->
           let (s, _, _, _) = mk_record_type ty in
           s
         | _ when FSharpType.IsFunction ty ->
           let (d,r) = FSharpType.GetFunctionElements(ty)
           z3.MkArraySort(mk_type d, mk_type r)
         | _ when ty = typeof<obj> -> obj_sort
         | _ -> z3.MkSort(ty.ToString())


     let get_type e = z3.GetSort(e) 
     let mk_tuple args = 
         let types = Array.ofList (List.map get_type args) in
         let (s, mk_tup, projs) = mk_tuple_type types in
         z3.MkApp(mk_tup, Array.ofList args)
                         
     let mk_union (uinfo:UnionCaseInfo) args = 
         let (s, cases) = mk_union_type uinfo.DeclaringType in
         let uc = List.find (fun (uinfo2 : UnionCaseInfo, _, _, _) -> uinfo2.Equals(uinfo)) cases in
         let (_, constr, tester, accessors) = uc in 
         z3.MkApp(constr, Array.ofList args)
         
     let mk_union_test (uinfo:UnionCaseInfo) (arg:Term) =
         let (s, cases) = mk_union_type uinfo.DeclaringType in
         let uc = List.find (fun (uinfo2 : UnionCaseInfo, _, _, _) -> uinfo2.Equals(uinfo)) cases in
         let (_, constr, tester, accessors) = uc in 
         z3.MkApp(tester, arg)     
         
     // Retrieve the projection function corresponding to a record accessor
     let get_field pi fields (projs : FuncDecl array)= 
         let rec get idx = 
             if idx >= Array.length fields then raise (System.Exception "not found") else
                if fields.[idx].Equals(pi) then 
                   (projs.[idx])
                else get (idx+1)
         in get 0

     // Retrieve the projection function corresponding to a data-type accessor
     let rec get_data_type_projection (pi:System.Reflection.PropertyInfo) cases =
         match cases with
         | [] -> raise (System.Exception "data type projection not found")
         | case::cases -> 
           let (ui, constr, test, acc : 'a array) = case
           let fields = (ui:UnionCaseInfo).GetFields()
           let rec find_field idx = 
               if idx >= fields.Length then None else
               if fields.[idx].Equals(pi) then Some idx else
               find_field (idx+1) 
           match find_field 0 with
           | None -> get_data_type_projection pi cases
           | Some idx -> acc.[idx]

     //
     // cache to memoize expressions that are built. Just in case someone is
     // using sharing.
     //
     let expr_cache = System.Collections.Generic.Dictionary<Expr,Term>()

     let eq_var (u:Var) (v:Var) = u.Equals(v)
     let env0 u = raise (System.Exception (Printf.sprintf "Variable %O not found" u))
     let mk_env0() = expr_cache.Clear(); env0
     let add_env env v e = expr_cache.Clear(); (fun u -> if eq_var u v then e else env u)
     let mk_env v e  = add_env env0 v e
     let mk_env_list assoc = List.fold (fun env (v,t) -> add_env env v t) env0 assoc
         
     let mk_var (e : Expr) = 
         let s = mk_type e.Type in
         z3.MkConst(e.ToString(), s)
         
     let matchargs varss argss = 
         List.length varss = List.length argss &&
         List.forall2 (fun x y -> List.length x = List.length y) varss argss
         
     let rec mk_expr env e =  memoize expr_cache e (mk_expr_mem env)
     and mk_expr_mem env e = 
         match e with
         | ExprShape.ShapeVar var -> env var                
         | PluginQ(mk_result) -> 
           let context = {mke = mk_expr env; mkt = mk_type; env = env; add_env = add_env; 
                          mke_env = mk_expr; env0 = mk_env0} 
           mk_result context
         | ExprShape.ShapeLambda(v,f) -> mk_expr env (array_plugin.mk_lift v f)
         | Patterns.NewTuple(args)     -> mk_tuple (List.map (mk_expr env) args)
         | Patterns.NewUnionCase(uinfo, args) -> mk_union uinfo (List.map (mk_expr env) args)
         | Patterns.TupleGet(e, idx) -> 
           let (_, _, fields) = mk_tuple_type1 e.Type in
           z3.MkApp(fields.[idx], mk_expr env e)
         | Patterns.PropertyGet(Some e1,pi,[]) when FSharpType.IsRecord e1.Type ->   
           let (_,_, fields, projs) = mk_record_type e1.Type in
           z3.MkApp(get_field pi fields projs, mk_expr env e1)
         | Patterns.PropertyGet(Some e1,pi,[]) when union_types.ContainsKey(e1.Type) ->   
           let (s, cases) = union_types.[e1.Type]
           z3.MkApp(get_data_type_projection pi cases, mk_expr env e1)
         | Patterns.AddressOf _ ->                  raise (System.Exception "Side-effect expressions are not handled")
         | Patterns.AddressSet _ ->                 raise (System.Exception "Side-effect expressions are not handled")
         | Patterns.FieldSet(_,_,_) ->              raise (System.Exception "Side-effect expressions are not handled")
         | Patterns.ForIntegerRangeLoop(_,_,_,_) -> raise (System.Exception "Side-effect expressions are not handled")
         | Patterns.WhileLoop(_,_) ->               raise (System.Exception "Side-effect expressions are not handled")
         | Patterns.Sequential(e1,e2) ->            raise (System.Exception "Side-effect expressions are not handled")
         | Patterns.TryWith(e1,v1,e2,v2,e3) ->      raise (System.Exception "Side-effect expressions are not handled")
         | Patterns.TryFinally(e1,e2) ->            raise (System.Exception "Side-effect expressions are not handled")
         | Patterns.PropertySet(e1,pi,args,vl) ->       raise (System.Exception "Side-effect expressions are not handled")
         | Patterns.VarSet(v,e) ->                  raise (System.Exception "Side-effect expressions are not handled")
         | Patterns.Coerce(e1,ty) ->                raise (System.Exception "TBD")
         | Patterns.FieldGet(e1,fi) ->              raise (System.Exception "TBD")
         | Patterns.LetRecursive(bindings, body) -> raise (System.Exception "TBD")
         | Patterns.NewArray(ty, args) ->           raise (System.Exception "TBD")
         | Patterns.NewDelegate(ty, args, body) ->  raise (System.Exception "TBD")
         | Patterns.NewObject(ci,args) ->           raise (System.Exception "TBD")
         | Patterns.Quote(e1) ->                    raise (System.Exception "TBD")
         | Patterns.TypeTest(e1,ty) ->              raise (System.Exception "TBD")         
         | Patterns.DefaultValue(ty) ->             mk_var e
         | Patterns.NewRecord(ty, args) ->          
           let (s, mk_rec, fields, projs) = mk_record_type ty in
           z3.MkApp(mk_rec, Array.ofList (List.map (mk_expr env) args))    
         | Patterns.Application(ExprShape.ShapeLambda(var, body), arg) 
         | Patterns.Let(var, arg, body) -> 
           let env = add_env env var (mk_expr env arg) in
           mk_expr env body
         | DerivedPatterns.Applications(DerivedPatterns.Lambdas(varss, body), argss) when matchargs varss argss -> 
           let rec addargs varss argss env = 
               match varss, argss with
               | vars::varss, args::argss -> addargs varss argss (addarg vars args env)
               | _, _ -> env
           and addarg vars args env = 
               match vars, args with
               | var::vars, arg::args -> addarg vars args (add_env env var (mk_expr env arg))
               | _, _ -> env
           let env = addargs varss argss env
           mk_expr env body
         | DerivedPatterns.Applications(DerivedPatterns.Lambdas(varss, body), argss)  ->
           Printf.printf "Applications don't match args\n";
           mk_var e
         | Patterns.IfThenElse(t, e1, e2) -> z3.MkIte(mk_expr env t, mk_expr env e1, mk_expr env e2)
         | Patterns.UnionCaseTest(e, uinfo) -> mk_union_test uinfo (mk_expr env e)  
         | GenericEqualityQ(_,_,[e1;e2]) 
         | EqualsQ(_,_,[e1;e2]) -> z3.MkEq(mk_expr env e1, mk_expr env e2)
         | NotEqQ(_,_,[e1;e2]) ->  z3.MkNot(z3.MkEq(mk_expr env e1, mk_expr env e2))         
         | ReflectedCall(md, args) -> mk_expr env (Expr.Applications(md, List.map (fun a -> [a]) args))          
         | Patterns.Value(ob,ty) ->   
           mk_call env e // raise (System.Exception "TBD")               
         | ExprShape.ShapeCombination(f, args) -> mk_call env  e
         | Patterns.Application(f,g) -> z3.MkArraySelect(mk_expr env f, mk_expr env g)
     and mk_call env e = 
         match e with
         | Patterns.Call(None,mi,args) ->
           let range = mk_type e.Type
           let doms  = List.map (fun (e:Expr) -> mk_type e.Type) args
           let args  = List.map (mk_expr env) args
           let f     = z3.MkFuncDecl(mi.Name, Array.ofList doms, range)
           z3.MkApp(f, Array.ofList args)
         | Patterns.Application(f,g) -> 
           z3.MkArraySelect(mk_expr env f, mk_expr env g)
         | _ -> 
           Printf.printf "Warning: not translating '%O'\n" e;
           mk_var e 
         
     let check is_prove e = 
         z3.Push()
         let env = mk_env0()
         let e = mk_expr env e
         if !Debug.debug then Printf.printf "%O\n" e
         let m = ref (null : Model)
         if is_prove then 
            z3.AssertCnstr(z3.MkNot(e))
         else
            z3.AssertCnstr(e)
         match z3.CheckAndGetModel(m) with
         | LBool.True ->  Printf.printf "not proved\n"
         | LBool.False -> Printf.printf "proved\n"
         | _ -> Printf.printf "not proved, unknown\n"
         if !m <> null then 
             (!m).Display(System.Console.Out)
             (!m).Dispose()                
         z3.Pop()
     
     member this.translate_expr (e : Expr) = mk_expr (mk_env0()) e
     
     member this.translate_expr_env1 v t (e : Expr) = mk_expr (mk_env v t) e
     
     member this.translate_expr_env_list assoc e = mk_expr (mk_env_list assoc) e

     member this.translate_type ty = mk_type ty
                    
     member this.prove (e : bool Expr) = check true e

     member this.check_sat e = check false e 
          
     member this.add_plugin p = plugins <- p::plugins   
            
     member this.getZ3 = z3
     
     end
     
module Solver = 

  let parameters = System.Collections.Generic.Dictionary<string,string>()
  let plugins    = System.Collections.Generic.Dictionary<string,Microsoft.Z3V3.Context -> Translate.IPlugin>()
  let show_stats = ref false
  let do_log     = ref false
  
  let set_parameter k v = parameters.[k] <- v
  
  let unset_parameter k = ignore(parameters.Remove(k))
  
  let add_plugin nm pl = plugins.[nm] <- pl
 
  let show_statistics v = show_stats := v
  
  let set_debug dbg = Debug.debug := dbg
  
  let log() = do_log := true
  
  let check is_prove e = 
      use cfg = new Microsoft.Z3V3.Config()
      cfg.SetParamValue("MODEL","true")
      for kv in parameters do
        cfg.SetParamValue (kv.Key, kv.Value)
      done
      use z3 = new Microsoft.Z3V3.Context(cfg) 
      if !do_log then 
         ignore (Microsoft.Z3V3.Z3Log.Open("quotations.z3")) 
      let ctx = Translate.Translator(z3)
      for kv in plugins do
        ctx.add_plugin (kv.Value(z3))
      done
      if is_prove then 
         ctx.prove e
      else
         ctx.check_sat e
      if !show_stats then 
          ctx.getZ3.DisplayStatistics(System.Console.Out)
   
  let prove e = check true e
          
  let check_sat e = check false e
  
  let simplify e = 
      use cfg = new Microsoft.Z3V3.Config()
      for kv in parameters do
        cfg.SetParamValue (kv.Key, kv.Value)
      done
      use z3 = new Microsoft.Z3V3.Context(cfg)      
      let ctx = Translate.Translator(z3)
      for kv in plugins do
        ctx.add_plugin (kv.Value(z3))
      done
      let r = z3.Simplify(ctx.translate_expr e)
      Printf.printf "%O\n" r
          
///
/// The plugin architecture is open-ended.
/// User code can add their own plugins.
/// We here provide an example custom plugin.
/// 
open DerivedPatterns
type MyMapPlugin(z3:Microsoft.Z3V3.Context) = class
  inherit Translate.IPlugin()
  let (|AddQ|_|) = (|SpecificCall|_|) <@ Map.add @>
  let (|EmptyQ|_|) = (|SpecificCall|_|) <@ Map.empty @>
  let (|TryFindQ|_|) = (|SpecificCall|_|) <@ Map.tryFind @>
  let (|FindQ|_|) = (|SpecificCall|_|) <@ Map.find @>
  let (|IsEmptyQ|_|) = (|SpecificCall|_|) <@ Map.isEmpty @>
  let (|RemoveQ|_|) = (|SpecificCall|_|) <@ Map.remove @>
  let (|ContainsQ|_|) = (|SpecificCall|_|) <@ Map.containsKey @>
        
  let mk_option ty = 
      use none = z3.MkConstructor("None", "TestNone", [||], [||], [||])
      use some = z3.MkConstructor("Some", "TestSome", [|"Value"|], [| ty |], [| 0ul |])
      let name = "Option`1"
      let s = z3.MkDataType(name, [| none; some |])
      (s, z3.GetConstructor(none), 
          z3.GetTester(none),
          z3.GetConstructor(some), 
          z3.GetTester(some), 
          z3.GetAccessors(some).[0])   
   
      
  override this.mk_expr e = 
    match e with
    | AddQ(_,_,[k;v;m]) -> 
      Some (fun c -> 
            let (_, _, _, mk_some, _, _) = mk_option(c.mkt v.Type)
            let k = c.mke k
            let v = c.mke v
            let m = c.mke m
            z3.MkArrayStore(m,k,z3.MkApp(mk_some, v))
           )
    | EmptyQ(_,_,[]) -> 
      Some (fun c -> 
            let tys = e.Type.GetGenericArguments()
            let (domain, range) = (tys.[0], tys.[1])
            let (_, mk_none, _, _, _, _) = mk_option(c.mkt range)
            let dom = c.mkt domain
            z3.MkArrayConst(dom, z3.MkConst(mk_none)))   
    | TryFindQ(_,_, [v; m]) -> 
      Some (fun c -> z3.MkArraySelect(c.mke m, c.mke v))
    | FindQ(_,_, [v; m]) -> 
      Some (fun c -> 
            let tys = m.Type.GetGenericArguments()
            let (domain, range) = (tys.[0], tys.[1])
            let (_, _,_,_, _, get_some) = mk_option(c.mkt range)
            z3.MkApp(get_some, z3.MkArraySelect(c.mke m, c.mke v))
            )          
    | IsEmptyQ(_,_, [m]) -> 
      Some (fun c -> 
            let tys = m.Type.GetGenericArguments()
            let (domain, range) = (c.mkt tys.[0], c.mkt tys.[1])
            let (_, mk_none,_,_, _, _) = mk_option(range)
            z3.MkEq(z3.MkArrayConst(domain, z3.MkConst(mk_none)), c.mke m)           
            )
    | RemoveQ(_,_, [v;m]) -> 
      Some (fun c -> 
            let tys = m.Type.GetGenericArguments()
            let (domain, range) = (c.mkt tys.[0], c.mkt tys.[1])
            let (_, mk_none,_,_, _, _) = mk_option(range)
            z3.MkArrayStore(c.mke m, c.mke v, z3.MkConst(mk_none))          
            )
    | ContainsQ(_,_, [v;m]) -> 
      Some (fun c -> 
            let tys = m.Type.GetGenericArguments()
            let (domain, range) = (c.mkt tys.[0], c.mkt tys.[1])
            let (_, _, is_none,_, _, _) = mk_option(range)
            z3.MkNot(z3.MkApp(is_none, z3.MkArraySelect(c.mke m, c.mke v)))         
            )    
    | _ -> None

  override this.mk_type ty = 
     if ty.Name = "Map`2" then
        Some (fun mkt ->  
            let tys = ty.GetGenericArguments()
            let (domain, range) = (mkt tys.[0], mkt tys.[1])
            let (s, _, _, _, _, _) = mk_option(range)
            z3.MkArraySort(domain, s))
     else
        None
     
  end


module MkPlugin = 
  let mk_plugin z3 = (MyMapPlugin(z3)) :> Translate.IPlugin
  let _ = Solver.add_plugin "Map" mk_plugin
 
  
// TBDs:
// . reflect Z3 terms back (extract computational content and execute proofs :-).

