// This file is a script that can be executed with the F# Interactive.  

#r "Microsoft.Z3.dll"
#r "Microsoft.Z3.FSharpUtils.dll"

open Microsoft.Z3
open Microsoft.Z3.Quotations
open Arrays 
open Logic


// -----------------------------------------------------------------------------------
//
// Prove
//            x + 2 = y ==> f (read(write(a,x,3)) = f (y - x + 1)
//
let f x = Logic.underspecified<obj>()

do Solver.prove <@
                declare (fun x y a -> 
                       x + 2 = y ==> 
                           ((f(select (store a x 3) (y - 2))) = f (y - x + 1))
                       )
                 @>
;;

// -----------------------------------------------------------------------------------
//
// Prove
//            x + 3 = y ==> f (read(write(a,x,3)) = f (y - x + 1)
//                ^
//                |
// 
do Solver.prove <@
                declare (fun x y a -> 
                       x + 3 = y ==> 
                           ((f(select (store a x 3) (y - 2))) = f (y - x + 1))
                       )
                 @>
       
       
do Solver.prove <@
                declare (fun x y a -> 
                       x + 3I = y ==> 
                           ((f(select (store a x 3I) (y - 2I))) = f (y - x + 1I))
                       )
                 @>
       
;;

do Solver.prove <@
                declare (fun a b c -> 
                       ((a = b || a = c) && f(a) = a) ==> 
                           (f(f(b)) = a || f(c) = a)
                       )
                 @>

;;

// -----------------------------------------------------------------------------------
// Non-linear arithmetic
//

#r "FSharp.PowerPack.dll"
open Microsoft.FSharp.Math

do Solver.prove <@
                  declare (fun x y (z:BigRational) u -> 
                          (x > 1N && y > u*u && z > u*u) ==> (x + y + z + x*y*z > u*u))                      
                 @>
       
do Solver.prove <@
                      declare (fun x y  -> (x > 0N && y > x) ==> (y/x > 1N))                      
                 @>

;;
       
              
// -----------------------------------------------------------------------------------
// Data-types
//


do Solver.prove <@  forall (fun x -> not (None = Some x))  @>
       
// enumeration types       
type fruit = | Apple | Orange | Banana

do Solver.prove <@ (forall (fun x -> (x = Apple || x = Orange || x = Banana))) && (Apple <> Orange) @>       

// recursive data-structures
type 'a tree = | Leaf | Node of 'a tree * 'a * 'a tree

do Solver.prove <@ forall (fun x -> (Node(Leaf, x, Leaf) <> Leaf)) @>
       
do Solver.prove <@ forall (fun x -> forall (fun y -> Node(y, x, Leaf) <> y)) @>

// nested data-structures
do Solver.prove <@ forall (fun x -> forall (fun y -> Node(Leaf, Node(Leaf, x, Leaf), y) <> y)) @>

// Mutually recursive data-structures
type ping = | Pong of pong | Ping1 of int
and  pong = | Ping of ping | Pong1 of int
       
do Solver.prove <@ Ping(Pong(Pong1 0)) <> Pong1 1 @>       
       
// Standard ML list data-type       
do Solver.prove <@ forall (fun x -> [x;2] <> []) @>       
;;
       
       
                 
                    
// -----------------------------------------------------------------------------------
// Arrays
//


do Solver.prove <@ forall (fun (x,y) -> (is_finite x && is_finite y) ==> is_finite (union x y)) @>
       
do Solver.prove <@ forall (fun (a,i,v) -> (store a i v) i = v) @>       
       
do Solver.prove <@ forall (fun (i,v) -> (K v) i = v) @>
              
// Example custom plugin 
// (the array theory can handle a large subset of Map and Set operations)
do Solver.prove <@ Map.add 1I 2I Map.empty <> Map.empty @>


// Example custom plugin 
// (the array theory can handle a large subset of Map and Set operations)
do Solver.prove <@ Map.find 1I (Map.add 1I 2I Map.empty)  = 2I @>


do Solver.prove <@ declare (fun x y -> Map.find x (Map.add x y Map.empty)  = y) @>

// -----------------------------------------------------------------------------------
// An example adapted from Sriram Rajamani
//

type host = | F1 | F2 | S1 | S2 | L1 | L2 | P1 | P2 | P3 | P4
type vlan = | V1 | V2 | V3 | V4 | V5
type vlanuid = | U1 | U2
type host_type = | Faculty | Student | Phone | Laptop

[<ReflectedDefinition>]
let type_of h = 
    match h with
    | F1 | F2 -> Faculty
    | S1 | S2 -> Student
    | L1 | L2 -> Laptop
    | _ -> Phone
    
let vlan_of (h:host) = Logic.underspecified<vlan>()
let vlanuid_of (h:host) = Logic.underspecified<vlanuid>()
let is_assigned (v:vlan) = Logic.underspecified<bool>()
let is_assigned' (v:vlanuid) = Logic.underspecified<bool>()

do Solver.prove <@ 
                not (
                    forall (fun (i,j) -> 
                                ((vlan_of i = vlan_of j) && (vlanuid_of i = vlanuid_of j)) ==> (i = j)) &&
                    forall (fun (i,j) ->  
                                (vlan_of i = vlan_of j) ==> (type_of i = type_of j)) &&
                    is_assigned (vlan_of F1) &&
                    is_assigned (vlan_of F2) &&
                    is_assigned (vlan_of S1) &&
                    is_assigned (vlan_of S2) &&                    
                    is_assigned (vlan_of L1) &&
                    is_assigned (vlan_of L2) &&                                
                    is_assigned (vlan_of P1) &&
                    is_assigned (vlan_of P2) &&        
                    is_assigned (vlan_of P3) &&
                    is_assigned (vlan_of P4) &&  
                    is_assigned' (vlanuid_of F1) &&
                    is_assigned' (vlanuid_of F2) &&
                    is_assigned' (vlanuid_of S1) &&
                    is_assigned' (vlanuid_of S2) &&                    
                    is_assigned' (vlanuid_of L1) &&
                    is_assigned' (vlanuid_of L2) &&                                
                    is_assigned' (vlanuid_of P1) &&
                    is_assigned' (vlanuid_of P2) &&        
                    is_assigned' (vlanuid_of P3) &&
                    is_assigned' (vlanuid_of P4) &&  
                    (V1 = vlan_of F1)              
                )
            @>


