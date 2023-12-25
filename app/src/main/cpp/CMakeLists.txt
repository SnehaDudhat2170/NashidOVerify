cmake_minimum_required(VERSION 3.4.1)


file(GLOB jni_srcs
        "main-native-lib.cpp"
        )
add_library(main-native-lib SHARED ${jni_srcs})


