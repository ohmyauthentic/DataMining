package com.company;

import java.io.IOException;
import java.util.*;

public class Main {

//    private void displaySet( Set<TreeSet<String>> set){
//        Iterator<TreeSet<String>> it = set.iterator();
//        while(it.hasNext()){
//            System.out.println(it.next().toString());
//        }
//    }
//
//    public Set getSubSets(Set<String> set) {
//        Set<Set<String>> reSet = new HashSet<>();
//        reSet.add(set);
//        Iterator<String> it = set.iterator();
//        while(it.hasNext()){
//            Set<String> tempSet = new HashSet<>(set);
//            tempSet.remove(it.next());
//            if(tempSet.size()>0){
//                Set nextTempSet = getSubSets(tempSet);
//                if(nextTempSet.size()>0)
//                    reSet.addAll(nextTempSet);
//            }
//        }
//        return reSet;
//    }

    public static void main(String[] args) throws IOException {
        XLS xls = new XLS("test.xls");
        List data = xls.getData2();
//        Map data2 = xls.getData();
//        Aprior aprior = new Aprior(data2,1000);
//        aprior.getRules();
        FpGrowth fpGrowth = new FpGrowth(data,1000);
        fpGrowth.getRules();

    }
}
