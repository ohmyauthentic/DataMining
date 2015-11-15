package com.company;

import java.io.IOException;
import java.util.*;


/**
 * Created by candy on 2015/11/2.
 * MIN_SUPPORT
 * MIN_CONFIDENCE
 * FREQUENCE
 */
public class Aprior {
    private int minSupport;
    private int counts;
    private Map<String,TreeSet> dataMap;

    public Aprior(Map data,int min_support){
        dataMap = data;
        minSupport = min_support;
        counts = data.size();
    }

    private static void displaySet( Set<TreeSet<String>> nextCandidate){
        Iterator<TreeSet<String>> it = nextCandidate.iterator();
        while(it.hasNext()){
            System.out.println(it.next().toString());
        }
    }

    private static void displayMap(Map<TreeSet<String>,Integer> data){
        for (Map.Entry<TreeSet<String>,Integer>entry:data.entrySet()){
            System.out.println(entry.getKey().toString()+":::"+entry.getValue());
        }
    }

    /**
     * 生成一项集
     * @return
     */
    public Map getFirstCandidate(){
        Map<TreeSet<String>,Integer> firstCandidate = new HashMap<>();
        for (Map.Entry<String,TreeSet>entry:dataMap.entrySet()) {
            Iterator it=entry.getValue().iterator();
            while (it.hasNext()) {
                TreeSet<String> temp = new TreeSet<>();
                temp.add((String) it.next());
                if (firstCandidate.containsKey(temp)) {
                    firstCandidate.put(temp, firstCandidate.get(temp) + 1);
                } else {
                    firstCandidate.put(temp, 1);
                }
            }
        }
        firstCandidate = getSupportedItemset(firstCandidate);
        return firstCandidate;
    }

    /**
     * 获取下级候选集(迭代)
     * @param prioriCandidate
     * @return
     */
    public Map getNextCandidate(Map<TreeSet<String>,Integer> prioriCandidate){
        Set<TreeSet<String>> nextCandidate = new HashSet<>();
        for (Map.Entry<TreeSet<String>,Integer>entry1:prioriCandidate.entrySet()){
            TreeSet<String> tempSet = (TreeSet<String>) entry1.getKey().clone();
            for (Map.Entry<TreeSet<String>,Integer>entry2:prioriCandidate.entrySet()) {
                if (entry1.equals(entry2)) continue;
                TreeSet<String> subSet1 = (TreeSet<String>) entry1.getKey().headSet(entry1.getKey().last(), false);
                TreeSet<String> subSet2 = (TreeSet<String>) entry2.getKey().headSet(entry2.getKey().last(), false);
//                System.out.println(subSet1.size() + "::" + subSet2.size());
                if (subSet1.equals(subSet2) && subSet1.size() == (entry1.getKey().size() - 1)&&subSet2.size() == (entry2.getKey().size() - 1)) {
                    tempSet.add(entry2.getKey().last());
                    nextCandidate.add((TreeSet<String>) tempSet.clone());
                    tempSet.remove(entry2.getKey().last());
                }
            }
            tempSet.clear();
        }
        if(nextCandidate.isEmpty());
        else{
            prioriCandidate = Scanner(nextCandidate);
            prioriCandidate.putAll(getNextCandidate(prioriCandidate));
        }
        return prioriCandidate;
    }

    private static boolean AisContainsB(Set A, Set B){
        boolean result = true;
        Iterator it = B.iterator();
        while(it.hasNext()){
            if(A.contains(it.next()));else return false;
        }
        return result;
    }


    /**
     * 扫描数据集，统计候选集次数
     * @param nextCandidate
     * @return
     */
    private Map Scanner(Set<TreeSet<String>> nextCandidate){
        Map<TreeSet<String>,Integer> map = new HashMap<>();
        for (Map.Entry<String,TreeSet>entry:dataMap.entrySet()) {
            Iterator it = nextCandidate.iterator();
            while (it.hasNext()){
                TreeSet<String> tempSet = (TreeSet<String>) it.next();
                if(AisContainsB(entry.getValue(), tempSet)){
//                    System.out.println("......");
                    if (map.containsKey(tempSet)) {
                        map.put(tempSet, map.get(tempSet) + 1);
                    } else {
                        map.put(tempSet, 1);
                    }
                }
            }
        }
        map = getSupportedItemset(map);
        return map;
    }

    /**
     * 消除不符合条件的候选集
     * @param map
     * @return
     */
    private Map getSupportedItemset(Map<TreeSet<String>,Integer> map){
        Map<TreeSet<String>,Integer> supportItemSet = new HashMap<>();
        for(Map.Entry<TreeSet<String>,Integer>entry:map.entrySet()){
            if(entry.getValue() >= minSupport){
                supportItemSet.put(entry.getKey(),entry.getValue());
            }
        }
        return supportItemSet;
    }

    private Map getAllSubSets(Map<TreeSet<String>,Integer> candidate){
        Map<TreeSet<String>,Integer> newCandidate = new HashMap<>();
        Set<TreeSet<String>> tempSet = new HashSet<>();
        for(Map.Entry<TreeSet<String>,Integer> entry:candidate.entrySet()){
            tempSet.add(entry.getKey());
            tempSet.addAll(getSubSets(entry.getKey()));
        }
        candidate = Scanner(tempSet);
        return candidate;
    }

    public Set getSubSets(TreeSet<String> set) {
        Set<TreeSet<String>> reSet = new HashSet<>();
        reSet.add(set);
        Iterator<String> it = set.iterator();
        while(it.hasNext()){
            TreeSet<String> tempSet = new TreeSet<>(set);
            tempSet.remove(it.next());
            if(tempSet.size()>1){
                Set nextTempSet = getSubSets(tempSet);
                if(nextTempSet.size()>0)
                    reSet.addAll(nextTempSet);
            }
        }
        return reSet;
    }

    public void getConfidence(Map<TreeSet<String>,Integer> data){
        for (Map.Entry<TreeSet<String>,Integer>entry:data.entrySet()){
            divide2parts(entry.getKey(),data);
        }
    }
    public void divide2parts(TreeSet set,Map<TreeSet<String>,Integer> data){
        if(set.size()<2)return;
        List<String> list = new ArrayList<>(set);
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < Math.floor(list.size()/2); j++) {
                TreeSet<String> tempSet = new TreeSet<>();
                TreeSet<String> remainPart = new TreeSet<>(set);
                tempSet.add(list.get(i));
                if((i+j)<Math.floor(list.size())){
                    tempSet.add(list.get(i+j));
                }
                remainPart.removeAll(tempSet);
//                System.out.println(data.get(tempSet)+"/"+data.get(set));
                if(data.get(tempSet)>0) {
                    int confidence = data.get(set) * 100 / data.get(tempSet);
                    int support = data.get(set) * 100 / counts;
                    System.out.println(tempSet.toString() + "=>" + remainPart.toString() + "\t[" + support + "%," + confidence + "%]"+"\t"+data.get(set));
                }
            }
        }
    }

    public void getRules(){
        System.out.println("使用Apriori 支持度为："+minSupport+"得到的关联规则：");
        Map<TreeSet<String>,Integer> firstCandidate = getFirstCandidate();
        Map<TreeSet<String>,Integer> allFrequentSet = new HashMap<>(firstCandidate);
        allFrequentSet.putAll(getAllSubSets(getNextCandidate(firstCandidate)));
        getConfidence(allFrequentSet);
//        displayMap(allFrequentSet);
    }


    public static void main(String[]args) throws IOException {
        XLS xls = new XLS("test.xls");
        Map data = xls.getData();
        Aprior aprior = new Aprior(data,1000);
        aprior.getRules();
    }
}
