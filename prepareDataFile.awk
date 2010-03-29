# jnguyen - 2010-03-28
# The field separator is | and it cannot appear in the content of any field.
BEGIN {FS="|"}
# Print the documentId, newline, then the remaining fields with | stripped out.
# The fields will be separated by spaces.
{ print $1 "\n" $2, $3, $4, $5}
