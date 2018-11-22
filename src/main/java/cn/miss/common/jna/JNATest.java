package cn.miss.common.jna;

/**
 * @Author: zhoulinshun
 * @Description:
 * @Date: Created in 2018/10/12.
 */
public class JNATest {
    public static void main(String[] args) {
        CLibrary.INSTANCE.hello_jna("hello world\n");
        CLibrary.INSTANCE.printf("hello \n");
    }
}
