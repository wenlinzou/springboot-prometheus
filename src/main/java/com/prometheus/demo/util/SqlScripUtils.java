package com.prometheus.demo.util;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SqlScripUtils {

    /*

    +----------------------------------+-----------------+------------------------------------+------+---------------------+---------------------+---------------------+
    | id                               | account         | device_id                          | num  | bind_time           | create_time         | update_time         |
    +----------------------------------+-----------------+------------------------------------+------+---------------------+---------------------+---------------------+
    | 00002769e6a3445d8ff45a1b3808cfee | 1XXXX3940280680 | 0XXXXXXX04010221092019122600016648 |    7 | 2019-12-26 16:24:12 | 2020-01-10 04:58:56 | 2020-01-21 04:44:10 |
    | 000030b1c3834e5fbae2bc6e89a3c479 | 1XXXX4658014810 | 0XXXXXXX040102210319410915Y8014810 |    1 | 2019-10-16 21:07:49 | 2020-01-10 00:24:15 | NULL                |
    | 0000352713a54eb381a068f3d7ed3ef2 | 1XXXX4241684009 | CXXXXXXXFF040310032019072200000495 |    0 | 2019-11-16 21:29:45 | 2020-01-10 00:55:09 | NULL                |
    | 0000437d2d3b4ef7aa136a100b603723 | 1XXXX2110062688 | CXXXXXXXFF040310072020010300007301 |    0 | 2020-01-03 11:58:54 | 2020-01-10 05:10:22 | NULL                |
    | fffff4a693a44bc291c80cf00cef6b41 | 1XXXX4220023662 | 0XXXXXXX04010221062019112500009214 |    0 | 2019-11-25 15:47:26 | 2020-01-10 01:07:08 | NULL                |
    +----------------------------------+-----------------+------------------------------------+------+---------------------+---------------------+---------------------+
    814350 rows in set (3.16 sec)
    [END] 2020/2/13 14:33:46

    */

    /**
     *
     * @param readPath 文件路径
     * @param tableName 表名
     * @param tabCloums 字段名
     * @param startLine 数据起始行
     * @param endLine 数据末尾行
     * @return
     */
    private static List<String> readFile(String readPath, String tableName, String tabCloums, int startLine, int endLine) {
        try {
            List<String> inserts = new ArrayList<>();
            FileReader reader = new FileReader(readPath);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;

            int readcount = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().length() < 1) {
                    continue;
                }
                readcount++;
                if (readcount < startLine || readcount >= endLine) {
                    continue;
                }
                String[] arrays = line.split("\\|");
                if (arrays.length < 1) {
                    continue;
                }

                String id = arrays[1].trim().replaceAll("[\u0000]", "");
                String ctei = arrays[2].trim().replaceAll("[\u0000]", "");
                String deviceId = arrays[3].trim().replaceAll("[\u0000]", "");
                String num = arrays[4].trim().replaceAll("[\u0000]", "");
                String bindTime = arrays[5].trim().replaceAll("[\u0000]", "");
                String createTime = arrays[6].trim().replaceAll("[\u0000]", "");
                String updateTime = arrays[7].trim().replaceAll("[\u0000]", "");

                StringBuilder sqlContent = new StringBuilder();

                sqlContent.append("insert into ");
                sqlContent.append(tableName).append(" (");

                String[] cloums = tabCloums.split(",");
                for (int i = 0; i < cloums.length; i++) {
                    String tempCloum = cloums[i].trim();
                    if (i < cloums.length - 1) {
                        sqlContent.append(tempCloum).append(", ");
                    } else {
                        sqlContent.append(tempCloum);
                    }
                }


                sqlContent.append(") VALUES (");

                sqlContent.append("\'").append(id).append("\'").append(",");
                sqlContent.append("\'").append(ctei).append("\'").append(",");
                sqlContent.append("\'").append(deviceId).append("\'").append(",");
                sqlContent.append("\'").append(num).append("\'").append(",");
                sqlContent.append("\'").append(bindTime).append("\'").append(",");
                sqlContent.append("\'").append(createTime).append("\'").append(",");
                if ("NULL".equals(updateTime) || updateTime.length() < 8) {
                    sqlContent.append(updateTime).append(");");
                } else {
                    sqlContent.append("\'").append(updateTime).append("\'").append(");");
                }
                inserts.add(sqlContent.toString());
            }
            System.out.println("READ_COUNT=" + readcount);

            return inserts;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void writeFile(List<String> lines, String outFile) {
        if (null != lines) {
            try {
                FileWriter writer = new FileWriter(outFile);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                int writecount = 0;
                for (String line : lines) {
                    writecount++;
                    bufferedWriter.write(line + "\n");
                }
                bufferedWriter.flush();
                System.out.println("WIRTE_COUNT=" + writecount);
                System.out.println("WIRTE_File=" + outFile);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int startLine = 4;
        int endLine = 8;
        List<String> resp = readFile("/Users/xx/Documents/MyDemoCode/test.log", "t_device_info",
                "id, account, device_id, num, bind_time,create_time, update_time", startLine, endLine);
        String writeFile = "/Users/xx/Documents/MyDemoCode/db" + System.currentTimeMillis() + ".sql";
        writeFile(resp, writeFile);
    }
}

