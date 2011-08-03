package org.destecs.vdmj;

public class TestReplace
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String model ="\noperations\n test : () ==>()\ntest() == let v : A := new	A() in skip;\n"+
		"\ntest2 : () ==> () \n test2()== let s = A`op1() in skip;";
		
		System.out.println(VDMCO.patch(model));

	}

}
