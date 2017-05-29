# ascii-hanoiTower
A Java implementation of hanoi tower game and a lecture about recursion. You can choose to play or to see the recursive solution, with the help of an ascii interface and color prints to underline recursion steps:

move ( 4 pieces , source : 1 , helper : 2 , target : 3 ) 
    ┌┐        ┌┐        ╔╗    
   ▓▓▓▓       ││        ║║    
  ▓▓▓▓▓▓      ││        ║║    
 ▓▓▓▓▓▓▓▓     ││        ║║    
▓▓▓▓▓▓▓▓▓▓    ││        ║║    
────┴┴────────┴┴────────╨╨────
──SOURCE────HELPER────TARGET──
Not able to do this. Decompose the problem:
Step 1: move 3 disks to helper. First recursion:
move ( 3 pieces , source : 1 , helper : 3 , target : 2 ) 
    ┌┐        ╔╗        ┌┐    
   ▓▓▓▓       ║║        ││    
  ▓▓▓▓▓▓      ║║        ││    
 ▓▓▓▓▓▓▓▓     ║║        ││    
██████████    ║║        ││    
────┴┴────────╨╨────────┴┴────
──SOURCE────TARGET────HELPER──
...
