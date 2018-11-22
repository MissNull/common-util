package cn.miss.common.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @Author: zhoulinshun
 * @Description:
 * @Date: Created in 2018/10/12.
 */
public interface CLibrary extends Library {
    //    (Platform.C_LIBRARY_NAME)
    CLibrary INSTANCE = Native.loadLibrary("hello", CLibrary.class);

    void hello_jna(String format);

    void printf(String format, Object... args);

}
