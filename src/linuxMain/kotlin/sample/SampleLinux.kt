package sample

import kotlinx.cinterop.*
import platform.posix.close
import chapter4.*

fun hello(): String = "Hello, Kotlin/Native!"

@ExperimentalUnsignedTypes
fun main() {
    println(hello())
    chapter4()
}

//Chapter 4 samples
@ExperimentalUnsignedTypes
@Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
fun chapter4(): Int {

    val buf: Array<String> =
        arrayOf(
            "chapterTestString",
            "chapterTestString2",
            "chapterTestString3"
        )
    /*val buf: Array<CPointer<ByteVar>> = memScoped {
        arrayOf(
            "chapterTestString1".cstr.ptr,
            "chapterTestString2".cstr.ptr,
            "chapterTestString3".cstr.ptr
        )
    }*/
    val fd = open("chapter4File", O_RDWR or O_CREAT or O_TRUNC)
    if (fd == -1) {
        perror("open")
        return 1
    }
    memScoped {
        val iov = allocArray<iovec>(3)
//        val iov = arrayListOf<iovec>()
        for (i in 0..2) {
            iov[i].iov_base = buf[i].cstr.ptr
            iov[i].iov_len = strlen(buf[i]) + 1u
            println("for loop i is $i and buf[$i] is ${buf[i]} and value is ${buf[i]} and value length is ${strlen(buf[i]) + 1u}")
        }
        val nr = writev(fd, iov[0].ptr, 3)
        if (nr.equals(-1)) {
            perror("writev")
            return 1
        }
        println("wrote $nr bytes")
    }
    if (close(fd).equals(-1)) {
        perror("close")
        return 1
    }
    return 0
}