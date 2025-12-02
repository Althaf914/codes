import java.util.*;

public class LALR {

    static class Prod {
        String lhs;
        List<String> rhs;
        Prod(String a, List<String> b) { lhs=a; rhs=b; }
    }

    static class Item {
        String lhs;
        List<String> rhs;
        int dot;
        String look;
        Item(String a, List<String> b, int c, String d) {
            lhs=a; rhs=b; dot=c; look=d;
        }
        public boolean equals(Object o){
            Item x=(Item)o;
            return lhs.equals(x.lhs)&&rhs.equals(x.rhs)&&dot==x.dot&&look.equals(x.look);
        }
        public int hashCode(){ return Objects.hash(lhs,rhs, dot, look); }
    }

    static List<Prod> prods=new ArrayList<>();
    static Set<String> terms=new HashSet<>();
    static Set<String> nonterms=new HashSet<>();
    static Map<String,Set<String>> FIRST=new HashMap<>();
    static Map<String,Set<String>> FOLLOW=new HashMap<>();

    static Set<String> firstOf(List<String> alpha){
        Set<String> r=new HashSet<>();
        for(String s:alpha){
            r.addAll(FIRST.get(s));
            if(!FIRST.get(s).contains("ε")) return r;
        }
        r.add("ε");
        return r;
    }

    static List<Set<Item>> C=new ArrayList<>();
    static Map<Integer,Map<String,Integer>> GOTO=new HashMap<>();
    static Map<Integer,Map<String,String>> ACTION=new HashMap<>();

    public static void main(String[] args){

        Scanner sc=new Scanner(System.in);

        int n=sc.nextInt(); sc.nextLine();

        for(int i=0;i<n;i++){
            String line=sc.nextLine().trim();
            String[] p=line.split("->");
            String lhs=p[0].trim();
            List<String> rhs=Arrays.asList(p[1].trim().split(" "));
            prods.add(new Prod(lhs,rhs));
            nonterms.add(lhs);
        }

        for(Prod p:prods)
            for(String s:p.rhs)
                if(!nonterms.contains(s) && !s.equals("ε")) terms.add(s);

        for(String x:terms){
            FIRST.put(x,new HashSet<>());
            FIRST.get(x).add(x);
        }
        for(String x:nonterms) FIRST.put(x,new HashSet<>());
        boolean ch=true;
        while(ch){
            ch=false;
            for(Prod p:prods){
                int before=FIRST.get(p.lhs).size();
                FIRST.get(p.lhs).addAll(firstOf(p.rhs));
                if(FIRST.get(p.lhs).size()>before) ch=true;
            }
        }

        for(String x:nonterms) FOLLOW.put(x,new HashSet<>());
        FOLLOW.get(prods.get(0).lhs).add("$");

        ch=true;
        while(ch){
            ch=false;
            for(Prod p:prods){
                for(int i=0;i<p.rhs.size();i++){
                    String B=p.rhs.get(i);
                    if(nonterms.contains(B)){
                        List<String> beta=p.rhs.subList(i+1,p.rhs.size());
                        Set<String> fb=firstOf(beta);
                        int before=FOLLOW.get(B).size();
                        for(String t:fb) if(!t.equals("ε")) FOLLOW.get(B).add(t);
                        if(beta.size()==0||fb.contains("ε"))
                            FOLLOW.get(B).addAll(FOLLOW.get(p.lhs));
                        if(FOLLOW.get(B).size()>before) ch=true;
                    }
                }
            }
        }

        Prod start=prods.get(0);
        List<String> nr=new ArrayList<>();
        nr.add(start.lhs+"'");
        prods.add(0,new Prod(start.lhs+"'", Collections.singletonList(start.lhs)));
        nonterms.add(start.lhs+"'");

        closureInit();

        buildTables();

        System.out.println("Enter input tokens separated by space:");
        List<String> inp=new ArrayList<>(Arrays.asList(sc.nextLine().trim().split(" ")));
        inp.add("$");

        Stack<Integer> st=new Stack<>();
        st.push(0);
        int ip=0;

        while(true){
            int s=st.peek();
            String a=inp.get(ip);
            String act=ACTION.getOrDefault(s,new HashMap<>()).get(a);

            if(act==null){
                System.out.println("Rejected");
                return;
            }

            if(act.startsWith("s")){
                int t=Integer.parseInt(act.substring(1));
                st.push(t);
                ip++;
            }
            else if(act.startsWith("r")){
                int k=Integer.parseInt(act.substring(1));
                Prod p=prods.get(k);
                for(int i=0;i<p.rhs.size();i++)
                    if(!p.rhs.get(i).equals("ε")) st.pop();
                int s2=st.peek();
                st.push(GOTO.get(s2).get(p.lhs));
            }
            else if(act.equals("acc")){
                System.out.println("Accepted");
                return;
            }
        }
    }

    static void closureInit(){
        Item start=new Item(prods.get(0).lhs,prods.get(0).rhs,0,"$");
        Set<Item> I0=closure(Set.of(start));
        C.add(I0);

        boolean ch=true;
        while(ch){
            ch=false;
            int size=C.size();
            for(int i=0;i<size;i++){
                Set<Item> I=C.get(i);
                Set<String> symbols=new HashSet<>();
                for(Item it:I){
                    if(it.dot<it.rhs.size()) symbols.add(it.rhs.get(it.dot));
                }
                for(String X:symbols){
                    Set<Item> J=GOTO(I,X);
                    if(J.size()==0) continue;
                    if(!C.contains(J)){
                        C.add(J);
                        ch=true;
                    }
                }
            }
        }
    }

    static Set<Item> closure(Set<Item> I){
        Set<Item> C=new HashSet<>(I);
        boolean ch=true;

        while(ch){
            ch=false;
            Set<Item> newI=new HashSet<>();
            for(Item it:C){
                if(it.dot<it.rhs.size()){
                    String B=it.rhs.get(it.dot);
                    if(nonterms.contains(B)){
                        List<String> beta=new ArrayList<>();
                        for(int i=it.dot+1;i<it.rhs.size();i++) beta.add(it.rhs.get(i));
                        beta.add(it.look);

                        Set<String> fl=firstOf(beta);
                        for(Prod p:prods){
                            if(p.lhs.equals(B)){
                                for(String l:fl){
                                    Item nx=new Item(B,p.rhs,0,l);
                                    if(!C.contains(nx)) newI.add(nx);
                                }
                            }
                        }
                    }
                }
            }
            if(!newI.isEmpty()){
                C.addAll(newI);
                ch=true;
            }
        }
        return C;
    }

    static Set<Item> GOTO(Set<Item> I,String X){
        Set<Item> J=new HashSet<>();
        for(Item it:I){
            if(it.dot<it.rhs.size() && it.rhs.get(it.dot).equals(X)){
                J.add(new Item(it.lhs,it.rhs,it.dot+1,it.look));
            }
        }
        return closure(J);
    }

    static void buildTables(){
        for(int i=0;i<C.size();i++){
            ACTION.put(i,new HashMap<>());
            GOTO.put(i,new HashMap<>());
        }

        for(int i=0;i<C.size();i++){
            Set<Item> I=C.get(i);
            for(Item it:I){
                if(it.dot<it.rhs.size()){
                    String a=it.rhs.get(it.dot);
                    if(terms.contains(a)){
                        Set<Item> J=GOTO(I,a);
                        int j=C.indexOf(J);
                        ACTION.get(i).put(a,"s"+j);
                    }
                } else {
                    if(it.lhs.equals(prods.get(0).lhs) && it.look.equals("$")){
                        ACTION.get(i).put("$","acc");
                    } else {
                        int k=indexOfProd(it.lhs,it.rhs);
                        ACTION.get(i).put(it.look,"r"+k);
                    }
                }
            }
            for(String A:nonterms){
                Set<Item> J=GOTO(C.get(i),A);
                int j=C.indexOf(J);
                if(j>=0) GOTO.get(i).put(A,j);
            }
        }
    }

    static int indexOfProd(String lhs,List<String> rhs){
        for(int i=0;i<prods.size();i++){
            if(prods.get(i).lhs.equals(lhs) && prods.get(i).rhs.equals(rhs))
                return i;
        }
        return -1;
    }
}