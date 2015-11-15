package com.company;

import javax.swing.tree.TreeNode;
import java.io.IOException;
import java.util.*;

/**
 * Created by candy on 2015/11/2.
 */
public class FpGrowth {
    private int minSupport;
    private List<List<String>> records;
    private int counts;

    private static void displayMap(Map<TreeSet<String>,Integer> data){
        for (Map.Entry<TreeSet<String>,Integer>entry:data.entrySet()){
            System.out.println(entry.getKey().toString()+":::"+entry.getValue());
        }
    }
    public FpGrowth(List<List<String>> dataMap,int minSupport) {
        this.minSupport = minSupport;
        this.records = dataMap;
        this.counts = records.size();
    }

    public Map getFirstCandidate(List<List<String>>records){
        Map<TreeSet<String>,Integer> firstCandidate = new HashMap<>();
        for(List<String> record:records){
            for(String item: record){
                TreeSet<String> temp = new TreeSet<>();
                temp.add(item);
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

    private Map getSupportedItemset(Map<TreeSet<String>,Integer> map){
        Map<TreeSet<String>,Integer> supportItemSet = new HashMap<>();
        for(Map.Entry<TreeSet<String>,Integer>entry:map.entrySet()){
            if(entry.getValue() >= minSupport){
                supportItemSet.put(entry.getKey(), entry.getValue());
            }
        }
        return supportItemSet;
    }

    /**
     * 构建（Fp-Tree） 1 项集
     *
     * @return
     */
    public ArrayList<FpNode> buildHeaderTable(List<List<String>>records) {
        ArrayList<FpNode> F1 = new ArrayList<>();
        Map<String, FpNode> map = new HashMap<>();
        if(records.size()<=0)return null;
        for (List<String> record:records) {
            for(String item:record) {
                if (!map.keySet().contains(item)) {
                    FpNode node = new FpNode(item);
                    node.setCount(1);
                    map.put(item, node);
                } else {
                    map.get(item).countIncrement(1);
                }
            }
        }
        Set<String> names = map.keySet();
        for (String name : names) {
            FpNode node2 = map.get(name);
            if (node2.getCount() >= minSupport) {
                F1.add(node2);
            }
        }
        Collections.sort(F1);
        return F1;
    }

    public FpNode buildFpTree(List<List<String>> records,ArrayList<FpNode>F1){
        FpNode root = new FpNode();
        for (List<String>record:records) {
            LinkedList<String> recordList = sortByF1(record,F1);
            FpNode subTreeRoot = root;
            FpNode tempRoot = null;
            if(root.getChildren() != null){
                while(!recordList.isEmpty()&&(tempRoot = subTreeRoot.findChild(recordList.peek()))!=null){
                    tempRoot.countIncrement(1);
                    subTreeRoot = tempRoot;
                    recordList.poll();
                }
            }
            addNodes(subTreeRoot, recordList, F1);
        }
        return root;
    }

    private LinkedList<String>sortByF1(List<String>record,ArrayList<FpNode>F1){
        Map<String,Integer>map = new HashMap<>();
        Iterator<String> it = record.iterator();
        while(it.hasNext()){
            String item = (String)it.next();
            for (int i = 0; i < F1.size(); i++) {
                FpNode tempNode = F1.get(i);
                if(tempNode.getName().equals(item)){
                    map.put(item,i);
                }
            }
        }
        ArrayList<Map.Entry<String,Integer>>al = new ArrayList<>(map.entrySet());
        Collections.sort(al, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o1.getValue() - o2.getValue();
            }
        });
        LinkedList<String> rest = new LinkedList<>();
        for(Map.Entry<String,Integer> entry:al){
            rest.add(entry.getKey());
        }
        return rest;
    }

    public void addNodes(FpNode ancestor, LinkedList<String> record,
                         ArrayList<FpNode> F1) {
        if (record.size() > 0) {
            while (record.size() > 0) {
                String item = record.poll();
                FpNode leafnode = new FpNode(item);
                leafnode.setCount(1);
                leafnode.setParent(ancestor);
                ancestor.addChild(leafnode);

                for (FpNode f1 : F1) {
                    if (f1.getName().equals(item)) {
                        while (f1.getNext() != null) {
                            f1 = f1.getNext();
                        }
                        f1.setNext(leafnode);
                        break;
                    }
                }
                addNodes(leafnode, record, F1);
            }
        }
    }

    public Map FPGrowth(List<List<String>>records,List<String>frequences){
        Map<TreeSet<String>,Integer> resultMap = new HashMap<>();
        ArrayList<FpNode> headerTable = buildHeaderTable(records);
        FpNode treeRoot = buildFpTree(records, headerTable);
        if(treeRoot.getChildren()==null||treeRoot.getChildren().size()==0)return null;
        if(frequences!=null){
            for(FpNode header : headerTable){
                TreeSet<String> tempSet = new TreeSet<>();
                tempSet.add(header.getName());
//                System.out.print(header.getCount() + "\t" + header.getName()+";");
                for(String s : frequences){
//                    System.out.print(s+";");
                    tempSet.add(s);
                }
                resultMap.put(tempSet, header.getCount());
//                System.out.println();
            }
        }
        for(FpNode header : headerTable){
            List<String> newPostPattern = new LinkedList<>();
            newPostPattern.add(header.getName());
            if(frequences!=null)newPostPattern.addAll(frequences);
            List<List<String>> newRecords = new LinkedList<>();
            FpNode backnode = header.getNext();
            while(backnode!=null){
                int counter = backnode.getCount();
                List<String> prenodes = new ArrayList<>();
                FpNode parent = backnode;
                while((parent = parent.getParent()).getName() != null){
                    prenodes.add(parent.getName());
                }
                while(counter-- >0){
                    newRecords.add(prenodes);
                }
                backnode= backnode.getNext();
            }
            Map tempMap = FPGrowth(newRecords,newPostPattern);
            if(tempMap!=null)
                resultMap.putAll(tempMap);
        }
        return resultMap;
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
        //FPGrowth(records,null);
        System.out.println("使用FP-Growth 支持度为："+minSupport+"得到的关联规则：");
        Map<TreeSet<String>,Integer> firstCandidate = getFirstCandidate(records);
        Map<TreeSet<String>,Integer> allFrequentSet = new HashMap<>(firstCandidate);
        allFrequentSet.putAll(FPGrowth(records, null));
        getConfidence(allFrequentSet);
//        displayMap(allFrequentSet);
    }

    public static void main(String[]args) throws IOException {
        XLS xls = new XLS("test.xls");
        List data = xls.getData2();
        FpGrowth fpGrowth = new FpGrowth(data,1000);
        fpGrowth.getRules();
    }



}

class FpNode implements Comparable<FpNode>{

    private String name;
    private int count;
    private FpNode parent;
    private FpNode next;
    private List<FpNode>children;

    public FpNode() {
    }

    public FpNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public FpNode getParent() {
        return parent;
    }

    public void setParent(FpNode parent) {
        this.parent = parent;
    }

    public FpNode getNext() {
        return next;
    }

    public void setNext(FpNode next) {
        this.next = next;
    }

    public List<FpNode> getChildren() {
        return children;
    }

    public void setChildren(List<FpNode> children) {
        this.children = children;
    }

    public void addChild(FpNode child) {
        if (this.getChildren() == null) {
            List<FpNode> list = new ArrayList<FpNode>();
            list.add(child);
            this.setChildren(list);
        } else {
            this.getChildren().add(child);
        }
    }

    public FpNode findChild(String name) {
        List<FpNode> children = this.getChildren();
        if (children != null) {
            for (FpNode child : children) {
                if (child.getName().equals(name)) {
                    return child;
                }
            }
        }
        return null;
    }

    public void countIncrement(int n) {
        this.count += n;
    }

    @Override
    public int compareTo(FpNode o) {
        int count = o.getCount();
        return  count - this.count;
    }
}