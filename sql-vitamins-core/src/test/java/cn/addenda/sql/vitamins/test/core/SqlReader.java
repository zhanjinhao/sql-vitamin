package cn.addenda.sql.vitamins.test.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author addenda
 * @since 2022/8/29 22:19
 */
public class SqlReader {

  public static String[] read(String fileName) {
    try {
      List<String> sqlList = new ArrayList<>();
      BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
      String s;
      do {
        s = bufferedReader.readLine();
        if (s != null) {
          sqlList.add(s);
        }
      } while (s != null);
      return sqlList.toArray(new String[0]);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new String[]{};
  }

  public static String[] read(String... fileNames) {
    List<String> r = new ArrayList<>();
    for (String fileName : fileNames) {
      String[] read = read(fileName);
      r.addAll(Arrays.asList(read));
    }
    return r.toArray(new String[0]);
  }

  public static String[] read(String[] anotherSqls, String... fileNames) {
    List<String> r = new ArrayList<>(Arrays.asList(anotherSqls));
    for (String fileName : fileNames) {
      String[] read = read(fileName);
      r.addAll(Arrays.asList(read));
    }
    return r.toArray(new String[0]);
  }

  public static String[] read(String fileName, String[] anotherSqls) {
    String[] read = read(fileName);
    if (anotherSqls == null) {
      return read;
    }
    List<String> r = new ArrayList<>(Arrays.asList(anotherSqls));
    r.addAll(Arrays.asList(read));
    return r.toArray(new String[0]);
  }

}
