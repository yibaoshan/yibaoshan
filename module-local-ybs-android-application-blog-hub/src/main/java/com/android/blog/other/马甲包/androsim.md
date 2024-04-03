
官网对于 Androsim 的介绍

https://code.google.com/archive/p/androguard/wikis/Usage.wiki

https://code.google.com/archive/p/elsim/wikis/Similarity.wiki

```

The tool is used to get the similarities between two apps. The documentation is available here

``` axelle@caiman:~/softs/androguard$ ./androsim.py -h Usage: androsim.py [options]

Options: -h, --help show this help message and exit -i INPUT, --input=INPUT file : use these filenames -t THRESHOLD, --threshold=THRESHOLD define the threshold -c COMPRESSOR, --compressor=COMPRESSOR define the compressor -d, --display display all information about methods -n, --new don't calculate the similarity score with new methods -e EXCLUDE, --exclude=EXCLUDE exclude specific class name (python regexp) -s SIZE, --size=SIZE exclude specific method below the specific size -x, --xstrings display similarities of strings -v, --version version of the API -l LIBRARY, --library=LIBRARY use python library (python) or specify the path of the shared library) ```

```

```

option_0 = { 'name' : ('-i', '--input'), 'help' : 'file : use these filenames', 'nargs' : 2 }
option_1 = { 'name' : ('-t', '--threshold'), 'help' : 'specify the threshold (0.0 to 1.0) to know if a method is similar. This option will impact on the filtering method. Because if you specify a higher value of the threshold, you will have more associations', 'nargs' : 1 }
option_2 = { 'name' : ('-c', '--compressor'), 'help' : 'specify the compressor (BZ2, ZLIB, SNAPPY, LZMA, XZ). The final result depends directly of the type of compressor. But if you use LZMA for example, the final result will be better, but it take more time', 'nargs' : 1 }
option_4 = { 'name' : ('-d', '--display'), 'help' : 'display all information about methods', 'action' : 'count' }
option_5 = { 'name' : ('-n', '--new'), 'help' : 'calculate the final score only by using the ratio of included methods', 'action' : 'count' }
option_6 = { 'name' : ('-e', '--exclude'), 'help' : 'exclude specific class name (python regexp)', 'nargs' : 1 }
option_7 = { 'name' : ('-s', '--size'), 'help' : 'exclude specific method below the specific size (specify the minimum size of a method to be used (it is the length (bytes) of the dalvik method)', 'nargs' : 1 }
option_8 = { 'name' : ('-x', '--xstrings'), 'help' : 'display similarities of strings', 'action' : 'count'  }
option_9 = { 'name' : ('-v', '--version'), 'help' : 'version of the API', 'action' : 'count' }
option_10 = { 'name' : ('-l', '--library'), 'help' : 'use python library (python) or specify the path of the shared library)', 'nargs' : 1 }

```

## options

-d 显示所有关于 methods 的信息，-x 显示字符串的相似度

