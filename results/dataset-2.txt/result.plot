set key bottom box width 2 height 3 opaque
set term png
set output 'front.png'
set ylabel "Valor" 
set xlabel "Custo"
set key font ",12"
plot 'NSGA_II' using ($1):(-$2) title 'NSGA-II','MOSFBA' using ($1):(-$2) title 'MOSFBA','paretoFront' using ($1):(-$2) title 'Frente de Pareto' lc rgb "orange" pointtype 12 ps 2

