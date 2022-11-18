import openpyxl
import sys

wb_obj = openpyxl.load_workbook(sys.argv[2])

sheet_obj = wb_obj.active

rows_number = sheet_obj.max_row+1

print(rows_number)

f = open(sys.argv[1], 'r+')
f.truncate(0)
f.write(str(rows_number))
f.close()
