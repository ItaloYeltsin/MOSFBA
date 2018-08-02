setwd('~/IdeaProjects/Multi-objective Scalaring Function Based Algorithm/')
mosfba = read.table(file='mosfba.txt')
nsga = read.table(file='FUN')
plot(time, mosfba, type='l', xlab='t /s', ylab='s1')
