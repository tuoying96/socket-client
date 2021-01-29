import sys

if __name__ == '__main__':
    a = []
    for i in range(1, len(sys.argv)):
        a.append((sys.argv[i]))
    f = open("output.txt", 'w')

    try:
        solution = eval(a[0])
        result = " STATUS " + str(solution)
        f.write(result)
    except ZeroDivisionError:
        f.write(" ERR #DIV/0")

    f.close()