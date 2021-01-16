package verifier;

import jdd.bdd.BDD;

public class Rule {
	private static int idcounter=0;
	private int id;//编号
	private String type;
	private String port;//端口
	private String fieldmatch;
	private String feildhit;//击中域
	private int prior;//优先级
	private String group;//acl规则在哪个分组
	public BDD bdd;
	public int b_match;
	public int b_hit;
	/**
	 * @param t rule type
	 * @param p forward port
	 * @param m match field
	 * @param h hit field
	 * @param pr priority
	 * @param bdd BDD
	 */
	public Rule(String t,String p,String m,String h,int pr,String g,BDD bdd) {
		this.id=++idcounter;
		this.type = t;
		this.port=p;
		this.fieldmatch = m; 
		this.feildhit= h;
		this.prior=pr;
		this.group = g;
		this.bdd=bdd;
		this.b_hit=bdd.ref(bdd.minterm(feildhit));
		this.b_match=bdd.ref(bdd.minterm(fieldmatch));	
	}
	public Rule(Rule r) {
		this.id=++idcounter;
		this.port=r.getport();
		this.fieldmatch = r.getMatch();
		this.prior=r.getPrior();
		this.bdd=r.bdd;
		this.b_hit=bdd.ref(bdd.minterm(feildhit));
		this.b_match=bdd.ref(bdd.minterm(fieldmatch));
	}
	public int getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public String getport(){
		return port;
	}
	public int getPrior(){
		return prior;
	}
	public String getMatch(){
		return fieldmatch;
	}
	public String getHit(){
		return feildhit;
	}
	public String getGroup() {
		return group;
	}
	public void  changeHit(){
		this.feildhit=this.fieldmatch;
		this.b_hit=b_match;
	}
	public void clean()
	{
		bdd.deref(b_hit);
		bdd.deref(b_match);
	}
	public void printer() {
		System.out.println("rule"+id+"  "+"port:"+port+"  "+"prior:"+prior+"  "+"match(string):"+fieldmatch[0]);
		System.out.print("match(bdd):");
		bdd.printSet(b_match);
		System.out.println("hit(string):"+feildhit);
		System.out.print("hit(bdd)");
		bdd.printSet(b_hit);
	}
}
