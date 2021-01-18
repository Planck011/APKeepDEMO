package verifier;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import jdd.bdd.BDD;
import jdd.util.NodeName;
import sun.jvm.hotspot.ui.action.FindAction;

public class APKeep {
	public static void main(String[] args) {
		Set<String> vlans = new HashSet<>();
		Set<String> acls = new HashSet<>();
		Set<String> inters = new HashSet<>();
		Map<String, Set<String>> pMap = new HashMap<>();
		Map<String, Set<String>> aMap = new HashMap<>();
		String name = "CE1"; 
		String[] vs = {"7","2","3","4","6","5","8"};
		String[] is = {"GE0/0/1","GE0/0/2","GE0/0/3"};
		for(String v:vs) {
			vlans.add(v);
		}
		for(String i:is) {
			inters.add(name+i);
		}
		pMap.put("CE1GE0/0/1", new HashSet<>());
		pMap.put("CE1GE0/0/2", new HashSet<>());
		pMap.put("CE1GE0/0/3", new HashSet<>());
		pMap.get("CE1GE0/0/3").add("3002_permit");
		pMap.get("CE1GE0/0/2").add("3001_permit");
		pMap.get("CE1GE0/0/1").add("7");
		pMap.get("CE1GE0/0/1").add("8");
		aMap.put("3002_permit", new HashSet<>());
		aMap.put("3001_permit", new HashSet<>());
		aMap.get("3002_permit").add("2");
		aMap.get("3002_permit").add("6");
		aMap.get("3002_permit").add("7");
		aMap.get("3001_permit").add("3");
		aMap.get("3001_permit").add("4");
		aMap.get("3001_permit").add("5");
		acls.add("3001");
		acls.add("3002");
		APKeep ap = new APKeep();
		device d = new device(name, inters, vlans, acls, pMap, aMap, ap.bdd);
		ap.devices.add(d);
		String file = "C:\\Users\\puyun\\Desktop\\test\\workspace\\test1209\\src\\rules.txt";
		try {
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader((new FileReader(file))); 
			String str;
			while((str = in.readLine())!=null) {
				String[] ips = str.split("\\,");
				if(ips[0].equals("FW"))
				{
					String port = ips[1];
					String dst = ips[2].substring(0,24);
					int pr = 1;
					Rule r = new Rule(ips[0], port, dst, "false", pr, "FW", ap.bdd);
					ap.rules.add(r);
				}
				else if (ips[0].equals("ACL")) {
					String group = ips[1];
					String[] ip = ips[2].split(" ");
					String src = ip[0].substring(0,24)+"--------";
					String dst = ip[1].substring(0,24)+"--------";
					String match = dst+src;
					Rule r = new Rule("ACL", group+"_deny", match, "false", 1, group, ap.bdd);
					ap.rules.add(r);
				}
				
			}
		} catch (IOException e) {
			// TODO: handle exception
		}
		for(device n:ap.devices)
		{
			for(Rule r:ap.rules)
			{
				ap.Identify(r, n.rules);
			}
			Set<Integer> dd = ap.Update(n.ppm.Pred, n.ppm.Port);
			n.sendTointerfaces(ap.bdd);
			mapPrint(n.Pred_interface);
			mapPrint(n.Port_interface);
			System.out.println(dd);
		}
		ap.bdd.cleanup();
		System.out.println();
	}
	public BDD bdd;
	public Set<device> devices; //init device
	public ArrayList<Rule> rules;//init rules
	public ArrayList<Change> C ; // changes
	public Graph G ; //DFG
	public Set<Integer> D ; //transffered predicate set
	public Set<device> V ;
	private static boolean traversed = false;
	private static int loop_count ;
	private static int hole_count ;
	
	public APKeep() {
		this.bdd=new BDD(1000,100);
		bdd.createVars(64);
		this.devices = new HashSet<>();
		this.rules = new ArrayList<>();
		this.C = new ArrayList<>();
		this.G = new Graph();
		this.D = new HashSet<>();
		this.V = null;
	}
	/**
	 * @param device 设备拓扑信息文件路径
	 * @param rule 待插入规则信息文件路径
	 */
	public APKeep(String device,String rule) {
		// TODO Auto-generated constructor stub
		this.bdd=new BDD(1000,100);
		bdd.createVars(32);
//		this.devices = initDevice(device);
//		this.rules = initRule(rule);
		this.C = new ArrayList<>();
		this.G = new Graph();
		this.D = new HashSet<>();
		this.V = null;
	}
	public APKeep(String device,String rule,String dd) {
		// TODO Auto-generated constructor stub
		this.bdd=new BDD(1000,100);
		bdd.createVars(32);
//		this.devices = initDevice(device);
//		this.rules = initRule(rule);
//		setDevice(dd);
		this.C = new ArrayList<>();
		this.G = new Graph();
		this.D = new HashSet<>();
		this.V = null;
	}
	/**
	 * @param set 进行遍历验证不变量的初始节点集
	 */
	public void getStartNodes(Set<device> set)
	{
		V = new HashSet<>();
		for(device s:set)
			V.add(s);
	}
	
	public static  <E,T> void mapPrint(Map<E, Set<T>> map) {
		for(Entry<E, Set<T>> entry:map.entrySet())
		{
			System.out.println("key="+entry.getKey()+"  values="+entry.getValue());
		}
	}
	public  device Find(String s,Set<device> set)//find device by name
	{
		ArrayList<device> arr =  new ArrayList<>(set);
		for (device e : arr) {
			if(e.name.equals(s))
				return e;
		}	
		return null;
	}
	/**
	 * @param r  插入的一条规则
	 * @param R  当前设备的已有规则链表
	 */
	public  void  Identify(Rule r,Set<Rule> R) {
		
		r.changeHit();// r.hit <-- r.match;
		int and = bdd.ref(bdd.minterm(""));
		for (Rule rule : R) {
			if(!r.getGroup().equals(rule.getGroup()))
				continue;
			and = bdd.ref(bdd.and(r.b_hit, rule.b_hit));
			if(rule.getPrior()>r.getPrior()&&and!=0)
			{
				r.b_hit = bdd.ref(bdd.and(r.b_hit, rule.b_hit));
			}
			if(rule.getPrior()<r.getPrior()&&and!=0)
			{
				if(r.getport()!=rule.getport())
				{
					Change change = new Change(and, rule.getport(),r.getport(), bdd);//
//					change.printChange();
					boolean flag = false;
					for(Change c:C)
					{
						if(c.from.equals(change.from)&&c.to.equals(change.to)&&c.insertion==change.insertion)
							flag = true;
					}
					if(flag == false)
						C.add(change);//add to changes
//					System.out.println("-------->identify success!<--------");
				}
				rule.b_hit = bdd.ref(bdd.and(bdd.not(r.b_hit), rule.b_hit));
			}
		}
		bdd.deref(and);
		R.add(r);
		System.out.println("-------->insert one rule!<----------");
	}
	private  void Split(int p,int p1,int p2,Map<String, Set<Integer>> pre,Map<Integer, Set<String>> por,Set<Integer> d)
	{
		for(String temp_port:por.get(p))
		{
			pre.get(temp_port).add(p1);
			pre.get(temp_port).add(p2);
			pre.get(temp_port).remove(p);
			
		}
		Set<String> s1 = new HashSet<>();
		Set<String> s2 = new HashSet<>();
		s1.addAll(por.get(p));
		s2.addAll(por.get(p));
		por.put(p1, s1);
		por.put(p2, s2);
		por.remove(p);//unclear
		//d
		if(d.contains(p))
		{
			d.add(p1);
			d.add(p2);
			d.remove(p);
		}		
	}
	private  void Transfer(int p,String from ,String to,Map<String, Set<Integer>> pre,Map<Integer, Set<String>> por,Set<Integer> d)
	{
		pre.get(from).remove(p);
		pre.get(to).add(p);
		por.get(p).add(to);
		por.get(p).remove(from);
		//d
		d.add(p);
	}

	private  void Merge(int p1,int p2,int p,Map<String, Set<Integer>> pre,Map<Integer, Set<String>> por,Set<Integer> d)
	{

		for(String temp_port:por.get(p1))
		{
			pre.get(temp_port).add(p);
			pre.get(temp_port).remove(p1);
			pre.get(temp_port).remove(p2);
		}
		Set<String> s1 = new HashSet<>();
		s1.addAll(por.get(p1));
		por.put(p, s1);//unclear
		if(d.contains(p1)||d.contains(p2))
		{
			d.add(p);
			d.remove(p1);
			d.remove(p2);
		}
	}
	/**
	 * @param pre 当前设备端口谓词映射
	 * @param por 当前设备谓词端口映射
	 * @return 当前设备的转移谓词集d
	 * @throws ConcurrentModificationException 
	 */
	public  Set<Integer> Update(Map<String, Set<Integer>> pre,Map<Integer, Set<String>> por) {
		Set<Integer> d = new HashSet<>();
		try {
		for(Change c:C)
		{
			if(pre.get(c.from)==null||pre.get(c.to)==null)
				continue;
			for(int i=0;i<pre.get(c.from).size();i++)
			{
				Integer[] P = pre.get(c.from).toArray(new Integer[pre.get(c.from).size()]);
				int and=bdd.ref(bdd.and(P[i],c.insertion));
				if(and!=0)
				{
					if(and!=P[i])
					{
						Split(P[i], and, bdd.and(P[i], bdd.not(c.insertion)), pre, por, d);
					}
					
					Transfer(and, c.from, c.to, pre, por, d);
					for(Integer pp:pre.get(c.from))
					{
						if(pp!=P[i]&&por.get(pp).equals(por.get(P[i])))
						{
							Merge(P[i], pp, bdd.or(P[i], pp), pre, por, d);
						}
					}
					c.insertion=bdd.and(c.insertion, bdd.not(P[i]));
				}
			}
//			mapPrint(pre);//test por or pre
//			mapPrint(por);
//			System.out.println();
		}
		} catch (ConcurrentModificationException e) {
			// TODO: handle exception
			System.out.println("catch ConcurrentModificationException!");
		}
		return d;
	}
	public void updateACl() {
		
	}
	/**
	 * 创建转发图G(V,E,A)，将相关的V E A添加进G
	 * @param d 当前设备转移谓词集
	 * @param por 当前设备谓词端口映射
	 */
	public void ConstructDeltaForwardingGraph(Set<Integer> d,Map<Integer, Set<String>> por)
	{
		Set<device> V = G.V;
		Set<Edge> E = G.E;
		Map<Edge, Set<Integer>> A = G.A;
		for(Integer delta:d)
		{
			if(por.get(delta)==null)
				continue;
			for(String port:por.get(delta))
			{
				for(device s1:devices)
				{
					if(s1.findInterface(port))
					{
						if(!V.contains(s1))
						{
							V.add(s1);
						}
						for(device s2:devices)
						{
							if(s2.findConnect(port))
							{
								if(!V.contains(s2))
								{
									V.add(s2);
								}
								Edge e1 = getEdge(s1, s2, port, E);
//								if(!E.contains(e1))//
								if(e1==null)
								{
//									if(A.get(e1) != null) {
//										A.get(e1).clear();
//									}
//									else {
//										A.put(e1, new HashSet<>());
//										A.get(e1).clear();
//									}
//									E.add(e1);
									e1  = new Edge(s1, s2, port);
									if(A.get(e1)==null)
										A.put(e1, new HashSet<>());
									E.add(e1);
								}
								A.get(e1).add(delta);
							}
						}
					}
				}
			}
		}
	}
	private Edge getEdge(device s1,device s2,String port,Set<Edge> E)
	{
		for(Edge i:E)
		{
			if(i.from.name.equals(s1.name)&&i.fport.equals(port)&&i.to.name.equals(s2.name))
				return i;
		}
		return null;
	}
	/**
	 * traverse the graph checking invariants
	 */
	public  void CheckInvariants()
	{
		if(V==null)
		{
			System.err.println("please initailize start device set V");
			return;
		}
		System.out.println("\n---------------CheckingInvariants-------------");
		for(device s:V)
		{
			loop_count = 0;
			hole_count = 0;
			System.out.println("\n------------------------------------------");
			System.out.println("strat device:"+s.name);
			Set<Integer> pset = new HashSet<>();
			pset.addAll(D);
			Set<device> history = new HashSet<>();
			history.clear();
			traversed = false;//判断未初始化的参与遍历的节点s
			Traverse(s,pset,history);
			if(traversed==false)
				System.out.println("found blackhole by uninitialized device!please check the device:"+s.name);
			System.out.println("loop_count:"+loop_count+"  hole_count:"+hole_count);
		}
	}
	private  void Traverse(device s,Set<Integer> pSet,Set<device> history)
	{
		if(pSet.isEmpty())
			return;
		if(history.contains(s))
		{
			System.out.println("\n	-->found loop!\n");
			loop_count++;
			return;
		}
		//
		for (Edge e : G.E) {
			if(s.findInterface(e.fport)&&s.equals(e.from))//e.from.comtains(s)
			{
				System.out.println("	checking   "+e.from.name+"-->"+e.to.name+"...");
				traversed  = true;
				for(Integer p:e.to.ppm.Pred.get("default"))//判断节点default端口上的谓词是否包含了这条边的谓词，如果是则出现黑洞
				{
					for(Integer pp:G.A.get(e))
					{
						if(bdd.and(p, pp)!=0)//
						{
							System.out.println("\n-->found blackhole!");
							hole_count++;
							return;
						}
					}
				}
				Set<Integer> insert = setAnd(pSet, G.A.get(e));
				history.add(s);
				Traverse(e.to, insert, history);
				for(Integer i:insert)
					bdd.deref(i);
			}
		}
	}
	/**
	 * compute A(and)B by using BDD
	 * @param A pset
	 * @param B 边上的谓词集
	 * @return 两集合的交集
	 */
	private  Set<Integer> setAnd(Set<Integer> A,Set<Integer> B) {
		Set<Integer> set = new HashSet<>();
		for(Integer a:A)
		{
			for(Integer b:B)
			{
				int temp = bdd.ref(bdd.and(a, b));
				if(temp!=0)
				{
					set.add(temp);
					continue;
				}
				bdd.deref(temp);
			}
		}
		return set;
	}
	public void clean() {
		bdd.cleanup();
		this.clean();
	}
}

