require(effsize)
library(stringr)
setwd('~/IdeaProjects/Multi-objective Scalaring Function Based Algorithm')

data = read.table(header = TRUE, file='results/dataset-1.txt/metrics')

# MOSFBA M??trics
m_hvr_avg = mean(data$m_hvr);
m_hvr_sd = sd(data$m_hvr);

m_gd_avg = mean(data$m_gd);
m_gd_sd = sd(data$m_gd);

m_sp_avg = mean(data$m_sp);
m_sp_sd = sd(data$m_sp);

# NSGA-II M??trics
n_hvr_avg = mean(data$n_hvr);
n_hvr_sd = sd(data$n_hvr);

n_gd_avg = mean(data$n_gd);
n_gd_sd = sd(data$n_gd);

n_sp_avg = mean(data$n_sp);
n_sp_sd = sd(data$n_sp);

# U Test
ht_hvr = wilcox.test(data$m_hvr, data$n_hvr, exact = TRUE) 
ht_gd = wilcox.test(data$m_gd, data$n_gd, exact = TRUE)
ht_sp = wilcox.test(data$m_sp, data$n_sp, exact = TRUE)

ht_hvr = wilcox.test(data$m_hvr, data$n_hvr, exact = TRUE) 
ht_gd = wilcox.test(data$m_gd, data$n_gd, exact = TRUE)
ht_sp = wilcox.test(data$m_sp, data$n_sp, exact = TRUE)

# Eff Size
es_hvr = VD.A(data$m_hvr, data$n_hvr) 
es_gd = VD.A(data$m_gd, data$n_gd)
es_sp = VD.A(data$m_sp, data$n_sp)

es_hvr = VD.A(data$m_hvr, data$n_hvr) 
es_gd = VD.A(data$m_gd, data$n_gd)
es_sp = VD.A(data$m_sp, data$n_sp)

metrics = matrix(nrow = 1, ncol = 6)

metrics[1, 1] = str_c(round(m_hvr_avg,4), '$\\pm$', round(m_hvr_sd, 4)) 
metrics[1, 2] = str_c(round(m_gd_avg,4), '$\\pm$', round(m_gd_sd, 4)) 
metrics[1, 3] = str_c(round(m_sp_avg,4), '$\\pm$', round(m_sp_sd, 4)) 

metrics[1, 4] = str_c(round(n_hvr_avg,4), '$\\pm$', round(n_hvr_sd, 4)) 
metrics[1, 5] = str_c(round(n_gd_avg,4), '$\\pm$', round(n_gd_sd, 4)) 
metrics[1, 6] = str_c(round(n_sp_avg,4), '$\\pm$', round(n_sp_sd, 4)) 
noquote(metrics)
metrics

estatistc = matrix(nrow = 1, ncol=3)
estatistc[1,1] = str_c(ht_hvr$p.value, ' ', round(es_hvr$estimate, 4));
estatistc[1,2] = str_c(ht_gd$p.value, ' ', round(es_gd$estimate, 4));
estatistc[1,3] = str_c(ht_sp$p.value, ' ', round(es_sp$estimate, 4));
estatistc
noquote(estatistc)
elapsedTimeMean = mean(data$elapsedTime)
elapsedTimeMean
