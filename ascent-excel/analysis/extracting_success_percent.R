data = read.csv(file=file.choose(),sep=",")

# Create a frame to store result, use particle count as row name
result = data.frame(a=c(0:9))
row.names(result) = result$a

for (i in c(0:9)) {
  # Build a new column to store values for this iteration
  result[toString(i)] = 0
  for (p in c(0:9)) {
    d = data[data$iteration==i & data$particle==p,'result']
    result[toString(p),toString(i)] = sum(d) / length(d)
  }
}

result = result[colnames(result)!="a"]
