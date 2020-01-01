
cmake_minimum_required(VERSION 3.4.1)

add_library(native-lib
        SHARED
        src/main/cpp/native-lib.cpp)
add_library(reftest
        SHARED
        src/main/cpp/reftest.cpp)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)



set(abi-lib ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${CMAKE_ANDROID_ARCH_ABI}/libabi.so)

message("abi-lib is ${abi-lib}")
message("log-lib is ${log-lib}")

target_link_libraries(
        native-lib
        ${abi-lib}
        ${log-lib})
target_link_libraries(
        reftest
        ${log-lib})