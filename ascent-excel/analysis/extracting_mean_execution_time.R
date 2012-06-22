data = read.csv(file=file.choose(),sep=",")

# Create a frame to store output, use particle count as row name
output = data.frame(a=c(0:9))
row.names(output) = output$a

for (i in c(0:9)) {
  # Build a new column to store values for this iteration
  output[toString(i)] = 0
  for (p in c(0:9)) {
    mn = mean(data[data$iteration==i & data$particle==p,'time'])
    output[toString(p),toString(i)] = mn
  }
}

# Remove the straggler 'a' column
output = output[colnames(output)!="a"]




