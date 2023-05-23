module Trail
open System.Collections.Generic

type Trail() =
   let mutable trail = [[]]    
   member this.Push() = trail <- []::trail          
   member this.Pop() = 
       for undo in List.head trail do undo()
       trail <- List.tail trail          
   member this.Add undo = 
       trail <- (undo::List.head trail)::(List.tail trail)
