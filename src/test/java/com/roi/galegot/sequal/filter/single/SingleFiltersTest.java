package com.roi.galegot.sequal.filter.single;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.roi.galegot.sequal.common.Sequence;
import com.roi.galegot.sequal.util.ExecutionParametersManager;

public class SingleFiltersTest {

	private static SparkConf spc;
	private static JavaSparkContext jsc;

	private static String commLine = "+";

	@BeforeClass
	public static void setupSpark() throws IOException {
		spc = new SparkConf().setAppName("TFG").setMaster("local[*]");
		jsc = new JavaSparkContext(spc);
		jsc.setLogLevel("ERROR");
	}

	@AfterClass
	public static void stopSpark() {
		jsc.close();
	}

	@Test
	public void filterBaseP() {
		/*
		 * Length = 31 Quality = 25.29032258064516 GC = 19 GCP = 0.6129032258064516 NAmb
		 * = 0 NAmbP = 0 A = 5 | 0.1612903226 T = 7 | 0.2258064516 G = 9 | 0.2903225806
		 * C = 10 | 0.3225806452 AT = 2 | 0.06451612903
		 */
		String seq1s1 = "@cluster_2:UMI_ATTCCG";
		String seq1s2 = "TTTCCGGGGCACATAATCTTCAGCCGGGCGC";
		String seq1s4 = "9C;=;=<9@4868>9:67AA<9>65<=>591";
		Sequence seq1 = new Sequence(seq1s1, seq1s2, commLine, seq1s4);

		/*
		 * Length = 30 Quality = 14.566666666666666 GC = 13 GCP = 0.43333333333333335
		 * NAmb = 3 NAmbP = 0.1 A = 8 | 0.2666666667 T = 5 | 0.1666666667 G = 6 | 0.2 C
		 * = 7 | 0.2333333333 N = 3 | 0.1 U = 1 | 0.0333333333 AT = 2 | 0.0666666667
		 */
		String seq2s1 = "@cluster_8:UMI_CTTTGA";
		String seq2s2 = "TATCCUNGCAATANTCTCCGAACNGGAGAG";
		String seq2s4 = "1/04.72,(003,-2-22+00-12./.-.4";
		Sequence seq2 = new Sequence(seq2s1, seq2s2, commLine, seq2s4);

		/*
		 * Length = 29 Quality = 30.103448275862068 GC = 8 GCP = 0.27586206896551724
		 * NAmb = 5 NAmbP = 0.1724137931034483 A = 10 | 0.3448275862 T = 6 |
		 * 0.2068965517 G = 5 | 0.1724137931 C = 3 | 0.1034482759 N = 5 | 0.1724137931
		 * AT = 4 | 0.1379310345
		 */
		String seq3s1 = "@cluster_12:UMI_GGTCAA";
		String seq3s2 = "GCAGTTNNAGATCAATATATNNNAGAGCA";
		String seq3s4 = "?7?AEEC@>=1?A?EEEB9ECB?==:B.A";
		Sequence seq3 = new Sequence(seq3s1, seq3s2, commLine, seq3s4);

		/*
		 * Length = 28 Quality =20.964285714285715 GC = 12 GCP = 0.42857142857142855
		 * NAmb = 1 NAmbP = 0.03571428571428571 A = 7 | 0.25 T = 7 | 0.25 G = 4 |
		 * 0.1428571429 C = 8 | 0.2857142857 N = 1 | 0.03571428571 S = 1 | 0.03571428571
		 * AT = 2 | 0.07142857143
		 */
		String seq4s1 = "@cluster_21:UMI_AGAACA";
		String seq4s2 = "GGCATTGCAAAATTTNTTSCACCCCCAG";
		String seq4s4 = ">=2.660/?:36AD;0<14703640334";
		Sequence seq4 = new Sequence(seq4s1, seq4s2, commLine, seq4s4);

		/*
		 * Length = 27 Quality = 30.62962962962963 GC = 12 GCP = 0.4444444444444444 NAmb
		 * = 0 NAmbP = 0 A = 5 | 0.1851851852 T = 10 | 0.3703703704 G = 4 | 0.1481481481
		 * C = 8 | 0.2962962963 AT = 2 | 0.07407407407
		 */
		String seq5s1 = "@cluster_29:UMI_GCAGGA";
		String seq5s2 = "CCCCCTTAAATAGCTGTTTATTTGGCC";
		String seq5s4 = "8;;;>DC@DAC=B?C@9?B?CDCB@><";
		Sequence seq5 = new Sequence(seq5s1, seq5s2, commLine, seq5s4);

		JavaRDD<Sequence> original = jsc.parallelize(Arrays.asList(seq1, seq2, seq3, seq4, seq5));
		JavaRDD<Sequence> filtered;
		ArrayList<Sequence> list;
		SingleFilter filter = new BaseP();

		ExecutionParametersManager.setParameter("BaseP", "");
		ExecutionParametersManager.setParameter("BasePMinVal", "");
		ExecutionParametersManager.setParameter("BasePMaxVal", "");
		filtered = filter.validate(original);
		assertEquals(original.collect(), filtered.collect());

		ExecutionParametersManager.setParameter("BaseP", "A");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 5);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 5);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));
		assertTrue(list.contains(seq4));
		assertTrue(list.contains(seq5));

		ExecutionParametersManager.setParameter("BaseP", "");
		ExecutionParametersManager.setParameter("BasePMinVal", "0.3");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 5);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 5);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));
		assertTrue(list.contains(seq4));
		assertTrue(list.contains(seq5));

		ExecutionParametersManager.setParameter("BaseP", "");
		ExecutionParametersManager.setParameter("BasePMinVal", "");
		ExecutionParametersManager.setParameter("BasePMaxVal", "0.3");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 5);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 5);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));
		assertTrue(list.contains(seq4));
		assertTrue(list.contains(seq5));

		ExecutionParametersManager.setParameter("BaseP", "A");
		ExecutionParametersManager.setParameter("BasePMinVal", "0.26");
		ExecutionParametersManager.setParameter("BasePMaxVal", "");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("BaseP", "T");
		ExecutionParametersManager.setParameter("BasePMinVal", "");
		ExecutionParametersManager.setParameter("BasePMaxVal", "0.25");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 4);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 4);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));
		assertTrue(list.contains(seq4));

		ExecutionParametersManager.setParameter("BaseP", "C");
		ExecutionParametersManager.setParameter("BasePMinVal", "0.2");
		ExecutionParametersManager.setParameter("BasePMaxVal", "0.3");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 3);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 3);
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq4));
		assertTrue(list.contains(seq5));

		ExecutionParametersManager.setParameter("BaseP", "AT");
		ExecutionParametersManager.setParameter("BasePMinVal", "0.1");
		ExecutionParametersManager.setParameter("BasePMaxVal", "0.2");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("BaseP", "A|T|C|AT|G");
		ExecutionParametersManager.setParameter("BasePMinVal", "0.26|-1|0.2|0.05|-1");
		ExecutionParametersManager.setParameter("BasePMaxVal", "-1|0.25|0.3|0.1|-1");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq2));
	}
}
