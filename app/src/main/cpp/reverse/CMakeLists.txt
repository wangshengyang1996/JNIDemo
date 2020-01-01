cmake_minimum_required(VERSION 3.6)
add_library(reversetest
        SHARED
        reverse.cpp)
find_library(
        log-lib
        log)
target_link_libraries(reversetest ${log-lib})