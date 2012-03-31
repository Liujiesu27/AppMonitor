
package com.eee168.appmonitor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FileTools {
    private FileTools() {
    }

    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>();

    public static final String JPG = "jpg";

    public static final String PNG = "png";

    public static final String GIF = "gif";

    public static final String TIF = "tif";

    public static final String BMP = "bmp";

    public static final String DWG = "dwg";

    public static final String HTML = "html";

    public static final String RTF = "rtf";

    public static final String XML = "xml";

    public static final String ZIP = "zip";

    public static final String RAR = "rar";

    public static final String PSD = "psd";

    public static final String EML = "eml";

    public static final String DBX = "dbx";

    public static final String PST = "pst";

    public static final String OFFICE = "office";

    public static final String MDB = "mdb";

    public static final String WPD = "wpd";

    public static final String EPS = "eps";

    public static final String PS = "ps";

    public static final String PDF = "pdf";

    public static final String QDF = "qdf";

    public static final String PWL = "pwl";

    public static final String WAV = "wav";

    public static final String AVI = "avi";

    public static final String RAM = "ram";

    public static final String RM = "rm";

    public static final String MPG = "mpg";

    public static final String MOV = "mov";

    public static final String ASF = "asf";

    public static final String MID = "mid";

    public static final String DOC = "doc";

    public static final String DOCX = "docx";

    public static final String PPT = "ppt";

    public static final String PPTX = "pptx";

    public static final String XLS = "xls";

    public static final String XLSX = "xlsx";

    public static final String TXT = "txt";

    /*-----------------------------type----------------------------*/
    private static void getAllFileType() {
        FILE_TYPE_MAP.put(JPG, "FFD8FF"); // JPEG
        FILE_TYPE_MAP.put(PNG, "89504E47"); // PNG
        FILE_TYPE_MAP.put(GIF, "47494638"); // GIF
        FILE_TYPE_MAP.put(TIF, "49492A00"); // TIFF
        FILE_TYPE_MAP.put(BMP, "424D"); // Windows Bitmap
        FILE_TYPE_MAP.put(DWG, "41433130"); // CAD
        FILE_TYPE_MAP.put(HTML, "68746D6C3E"); // HTML
        FILE_TYPE_MAP.put(RTF, "7B5C727466"); // Rich Text Format
        FILE_TYPE_MAP.put(XML, "3C3F786D6C");
        FILE_TYPE_MAP.put(ZIP, "504B0304");
        FILE_TYPE_MAP.put(RAR, "52617221");
        FILE_TYPE_MAP.put(PSD, "38425053"); // PhotoShop
        FILE_TYPE_MAP.put(EML, "44656C69766572792D646174653A"); // Email
        // [thorough
        // only]
        FILE_TYPE_MAP.put(DBX, "CFAD12FEC5FD746F"); // Outlook Express
        FILE_TYPE_MAP.put(PST, "2142444E"); // Outlook
        FILE_TYPE_MAP.put(OFFICE, "D0CF11E0"); // office type，include doc、xls
                                               // and ppt
        FILE_TYPE_MAP.put(MDB, "000100005374616E64617264204A"); // MS Access
        FILE_TYPE_MAP.put(WPD, "FF575043"); // WordPerfect
        FILE_TYPE_MAP.put(EPS, "252150532D41646F6265");
        FILE_TYPE_MAP.put(PS, "252150532D41646F6265");
        FILE_TYPE_MAP.put(PDF, "255044462D312E"); // Adobe Acrobat
        FILE_TYPE_MAP.put(QDF, "AC9EBD8F"); // Quicken
        FILE_TYPE_MAP.put(PWL, "E3828596"); // Windows Password
        FILE_TYPE_MAP.put(WAV, "57415645"); // Wave
        FILE_TYPE_MAP.put(AVI, "41564920");
        FILE_TYPE_MAP.put(RAM, "2E7261FD"); // Real Audio
        FILE_TYPE_MAP.put(RM, "2E524D46"); // Real Media
        FILE_TYPE_MAP.put(MPG, "000001BA"); //
        FILE_TYPE_MAP.put(MOV, "6D6F6F76"); // Quicktime
        FILE_TYPE_MAP.put(ASF, "3026B2758E66CF11"); // Windows Media
        FILE_TYPE_MAP.put(MID, "4D546864"); // MIDI (mid)
    }

    /**
     * get the file type by read header of file
     * 
     * @param file
     * @return file type
     * @throws BaseException
     */
    public static String getFileType(File file) {
        getAllFileType();
        String fileType = null;
        FileInputStream is = null;
        String expandedName = "";
        String fileName = file.getName();
        // get extend name
        if (-1 != fileName.lastIndexOf(".")) {
            expandedName = fileName.substring(fileName.lastIndexOf(".") + 1);
        }

        try {
            is = new FileInputStream(file);
            byte[] b = new byte[16];
            is.read(b, 0, b.length);
            String filetypeHex = String.valueOf(bytesToHexString(b));
            Iterator<Entry<String, String>> entryiterator = FILE_TYPE_MAP.entrySet().iterator();
            while (entryiterator.hasNext()) {
                Entry<String, String> entry = entryiterator.next();
                String fileTypeHexValue = entry.getValue();
                if (filetypeHex.toUpperCase().startsWith(fileTypeHexValue)) {
                    fileType = entry.getKey();
                    if (OFFICE.equals(fileType)) {
                        fileType = getOfficeFileType(is);
                    } else if (ZIP.equals(fileType)
                            && (DOCX.equalsIgnoreCase(expandedName)
                                    || PPTX.equalsIgnoreCase(expandedName) || XLSX
                                        .equalsIgnoreCase(expandedName))) {
                        fileType = expandedName;// office2007
                    }
                    break;
                }
            }

            // if can not get fileType,set fileType as extend name
            if (fileType == null) {
                fileType = expandedName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return fileType;
    }

    /**
     * judge the office type
     * 
     * @param fileInputStream
     * @return file type
     * @throws BaseException
     */
    private static String getOfficeFileType(FileInputStream fileInputStream) {
        String officeFileType = DOC;
        byte[] b = new byte[512];
        try {
            fileInputStream.read(b, 0, b.length);
            String filetypeHex = String.valueOf(bytesToHexString(b));
            String flagString = filetypeHex.substring(992, filetypeHex.length());
            if (flagString.toLowerCase().startsWith("eca5c")) {
                officeFileType = DOC;
            } else if (flagString.toLowerCase().startsWith("fdffffff09")) {
                officeFileType = XLS;

            } else if (flagString.toLowerCase().startsWith("09081000000")) {
                officeFileType = XLS;
            } else {
                officeFileType = PPT;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return officeFileType;
    }

    /**
     * judge the MS Office type
     * 
     * @param fileType
     * @return
     */
    public static boolean isOfficeFileType(String fileType) {
        return DOC.equalsIgnoreCase(fileType) || DOCX.equalsIgnoreCase(fileType)
                || XLS.equalsIgnoreCase(fileType) || XLSX.equalsIgnoreCase(fileType)
                || PPT.equalsIgnoreCase(fileType) || PPTX.equalsIgnoreCase(fileType);
    }

    /**
     * get the header string of file
     * 
     * @param src
     * @return
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static String getExpandedName(File file) {
        String expandedName = "";

        String fileName = file.getAbsolutePath();

        if (-1 != fileName.lastIndexOf(".")) {
            expandedName = fileName.substring(fileName.lastIndexOf(".") + 1);
        }

        return expandedName;
    }

    /**
     * create file by byte[]
     * 
     * @param fileData
     * @param fileName absolute path
     * @return return true if success,else return false
     * @throws IOException
     */
    public static boolean createFile(byte[] fileData, String fileName) throws IOException {
        boolean flag = false;

        File file = new File(fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(fileData);
            flag = true;

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return flag;
    }

    /**
     * delete file or folder
     * 
     * @param fileName
     * @return return true if success,else return false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {

                return deleteFile(fileName);
            } else {
                return deleteDirectory(fileName);
            }
        }
    }

    /**
     * delete file
     * 
     * @param fileName
     * @return return true if success,else return false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        boolean flag = file.delete();
        if (!flag) {
            flag = forceDelete(file);
        }
        return flag;
    }

    public static boolean forceDelete(File f) {
        boolean result = false;
        int tryCount = 0;
        while (!result && tryCount++ < 10) {
            System.gc();
            result = f.delete();
        }
        return result;
    }

    /**
     * delete folder and files in the folder
     * 
     * @param dir
     * @return return true if success,else return false
     */
    public static boolean deleteDirectory(String dir) {
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            return false;
        }

        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * read file
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        byte[] bytes = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);

            long length = file.length();

            if (length > Integer.MAX_VALUE) {
                throw new IOException("File is too large " + file.getName());
            }

            bytes = new byte[(int) length];

            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = fis.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        return bytes;
    }

    public static List<String> getFilesFromPath(String path) {
        List<String> filelist = new ArrayList<String>();

        File pathFile = new File(path);
        if (pathFile.exists() && pathFile.isDirectory()) {
            String[] lists = pathFile.list();
            if (lists != null && lists.length > 0) {
                for (String s : lists) {
                    if (new File(path + File.separator + s).isFile()) {
                        filelist.add(s);
                    } else if (new File(path + File.separator + s).isDirectory()) {
                        filelist.addAll(getFilesFromPath(path + File.separator + s));
                    }
                }
            }
        }

        return filelist;
    }

    public static boolean appendDataToFile(File file, byte[] appendData) {
        if (file == null || !file.isFile() || appendData == null || appendData.length == 0) {
            return false;
        }

        boolean flag = false;
        try {
            byte[] data = getBytesFromFile(file);
            if (data == null) {
                return false;
            }

            byte[] newData = new byte[data.length + appendData.length];
            System.arraycopy(data, 0, newData, 0, data.length);
            System.arraycopy(appendData, 0, newData, data.length, appendData.length);
            String tempFile = file.getAbsolutePath() + ".temp";
            if (flag = createFile(newData, tempFile)) {
                // return new File(tempFile).renameTo(file);
                return flag;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return flag;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        byte[] appendData = new byte[] {
                0x01, 0x01, 0x01, 0x01, 0x01
        };
        File file = new File("/home/liujie/test/RemoteControl.apk");
        System.out.println(appendDataToFile(file, appendData));

    }

}
