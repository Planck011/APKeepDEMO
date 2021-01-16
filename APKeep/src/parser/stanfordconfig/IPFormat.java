package parser.stanfordconfig;

public class IPFormat {
    /*将ip地址转换成32位的二进制*/
    public static String toBinaryNumber(String ipAddress) {
        String[] octetArray = ipAddress.split("\\.");
        String binaryNumber = "";
        for (String str : octetArray) {
            int octet = Integer.parseInt(str, 10);
            String binaryOctet = Integer.toBinaryString(octet);
            int bolength = binaryOctet.length();
            if (bolength < 8) {
                for (int i = 0; i < 8 - bolength; i++) {
                    binaryOctet = '0' + binaryOctet;            //补前导0
                }
            }
            binaryNumber += (binaryOctet);
        }
        return binaryNumber;
    }

    //将掩码转换为二进制，注意在本系统提取的掩码是0.0.0.225形式，本方法将取反转为通用的掩码形式
    public static String MaskToBinaryNumber(String Mask) {
        String str = toBinaryNumber(Mask);
        str = str.replace('0', '2');
        str = str.replace('1', '0');
        str = str.replace('2', '1');
        return str;
    }


    public static int count_in_String(String str) {
        int fromIndex = 0;
        int count = 0;
        while (true) {
            int index = str.indexOf("1", fromIndex);
            if (-1 != index) {
                fromIndex = index + 1;
                count++;
            } else {
                break;
            }
        }
        return count;
    }
}
