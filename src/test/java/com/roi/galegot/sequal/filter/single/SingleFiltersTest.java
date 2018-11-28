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
		spc = new SparkConf().setAppName("SeQual").setMaster("local[*]");
		jsc = new JavaSparkContext(spc);
		jsc.setLogLevel("ERROR");
	}

	@AfterClass
	public static void stopSpark() {
		jsc.close();
	}

	@Test
	public void filterLength() {

		/*
		 * Length = 30
		 */
		String seq2s1 = "@cluster_8:UMI_CTTTGA";
		String seq2s2 = "TATCCUNGCAATANTCTCCGAACNGGAGAG";
		String seq2s4 = "1/04.72,(003,-2-22+00-12./.-.4";
		Sequence seq2 = new Sequence(seq2s1, seq2s2, commLine, seq2s4);

		/*
		 * Length = 29
		 */
		String seq3s1 = "@cluster_12:UMI_GGTCAA";
		String seq3s2 = "GCAGTTNNAGATCAATATATNNNAGAGCA";
		String seq3s4 = "?7?AEEC@>=1?A?EEEB9ECB?==:B.A";
		Sequence seq3 = new Sequence(seq3s1, seq3s2, commLine, seq3s4);

		/*
		 * Length = 28
		 */
		String seq4s1 = "@cluster_21:UMI_AGAACA";
		String seq4s2 = "GGCATTGCAAAATTTNTTSCACCCCCAG";
		String seq4s4 = ">=2.660/?:36AD;0<14703640334";
		Sequence seq4 = new Sequence(seq4s1, seq4s2, commLine, seq4s4);

		JavaRDD<Sequence> original = jsc.parallelize(Arrays.asList(seq2, seq3, seq4));
		JavaRDD<Sequence> emptyRdd = jsc.parallelize(new ArrayList<Sequence>());
		JavaRDD<Sequence> filtered;
		ArrayList<Sequence> list;
		SingleFilter filter = new Length();

		// Test for empty RDD
		filtered = filter.validate(emptyRdd);
		assertEquals(filtered.count(), 0);

		ExecutionParametersManager.setParameter("LengthMinVal", "");
		ExecutionParametersManager.setParameter("LengthMaxVal", "");
		filtered = filter.validate(original);
		assertEquals(original.collect(), filtered.collect());

		ExecutionParametersManager.setParameter("LengthMinVal", "29");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("LengthMinVal", "");
		ExecutionParametersManager.setParameter("LengthMaxVal", "29");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq3));
		assertTrue(list.contains(seq4));

		ExecutionParametersManager.setParameter("LengthMinVal", "29");
		ExecutionParametersManager.setParameter("LengthMaxVal", "29");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq3));
	}

	@Test
	public void filterQuality() {

		/*
		 * Quality = 25.29032258064516
		 */
		String seq1s1 = "@cluster_2:UMI_ATTCCG";
		String seq1s2 = "TTTCCGGGGCACATAATCTTCAGCCGGGCGC";
		String seq1s4 = "9C;=;=<9@4868>9:67AA<9>65<=>591";
		Sequence seq1 = new Sequence(seq1s1, seq1s2, commLine, seq1s4);

		/*
		 * Quality = 30.103448275862068
		 */
		String seq2s1 = "@cluster_12:UMI_GGTCAA";
		String seq2s2 = "GCAGTTNNAGATCAATATATNNNAGAGCA";
		String seq2s4 = "?7?AEEC@>=1?A?EEEB9ECB?==:B.A";
		Sequence seq2 = new Sequence(seq2s1, seq2s2, commLine, seq2s4);

		/*
		 * Quality =20.964285714285715
		 */
		String seq3s1 = "@cluster_21:UMI_AGAACA";
		String seq3s2 = "GGCATTGCAAAATTTNTTSCACCCCCAG";
		String seq3s4 = ">=2.660/?:36AD;0<14703640334";
		Sequence seq3 = new Sequence(seq3s1, seq3s2, commLine, seq3s4);

		JavaRDD<Sequence> original = jsc.parallelize(Arrays.asList(seq1, seq2, seq3));
		JavaRDD<Sequence> emptyRdd = jsc.parallelize(new ArrayList<Sequence>());
		JavaRDD<Sequence> filtered;
		ArrayList<Sequence> list;
		SingleFilter filter = new Quality();

		// Test for empty RDD
		filtered = filter.validate(emptyRdd);
		assertEquals(filtered.count(), 0);

		ExecutionParametersManager.setParameter("QualityMinVal", "");
		ExecutionParametersManager.setParameter("QualityMaxVal", "");
		filtered = filter.validate(original);
		assertEquals(original.collect(), filtered.collect());

		ExecutionParametersManager.setParameter("QualityMinVal", "21");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));

		ExecutionParametersManager.setParameter("QualityMinVal", "");
		ExecutionParametersManager.setParameter("QualityMaxVal", "30");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("QualityMinVal", "21");
		ExecutionParametersManager.setParameter("QualityMaxVal", "30");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq1));
	}

	@Test
	public void filterQualityScore() {
		/*
		 * 5 = 20 | 6 = 21
		 */
		String seq1s1 = "@cluster_2:UMI_ATTCCG";
		String seq1s2 = "TTTCCGGGGCACATAATCTTCAGCCGGGCGC";
		String seq1s4 = "6666666666666656666666666666666";
		Sequence seq1 = new Sequence(seq1s1, seq1s2, commLine, seq1s4);

		/*
		 * 6 = 21
		 */
		String seq2s1 = "@cluster_12:UMI_GGTCAA";
		String seq2s2 = "GCAGTTNNAGATCAATATATNNNAGAGCA";
		String seq2s4 = "66666666666666666666666666666";
		Sequence seq2 = new Sequence(seq2s1, seq2s2, commLine, seq2s4);

		/*
		 * 6 = 21 | 7 = 22
		 */
		String seq3s1 = "@cluster_21:UMI_AGAACA";
		String seq3s2 = "GGCATTGCAAAATTTNTTSCACCCCCAG";
		String seq3s4 = "6666666666666676666666666666";
		Sequence seq3 = new Sequence(seq3s1, seq3s2, commLine, seq3s4);

		JavaRDD<Sequence> original = jsc.parallelize(Arrays.asList(seq1, seq2, seq3));
		JavaRDD<Sequence> emptyRdd = jsc.parallelize(new ArrayList<Sequence>());
		JavaRDD<Sequence> filtered;
		ArrayList<Sequence> list;
		SingleFilter filter = new QualityScore();

		// Test for empty RDD
		filtered = filter.validate(emptyRdd);
		assertEquals(filtered.count(), 0);

		ExecutionParametersManager.setParameter("QualityScoreMinVal", "");
		ExecutionParametersManager.setParameter("QualityScoreMaxVal", "");
		filtered = filter.validate(original);
		assertEquals(original.collect(), filtered.collect());

		ExecutionParametersManager.setParameter("QualityScoreMinVal", "21");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("QualityScoreMinVal", "");
		ExecutionParametersManager.setParameter("QualityScoreMaxVal", "21");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));

		ExecutionParametersManager.setParameter("QualityScoreMinVal", "21");
		ExecutionParametersManager.setParameter("QualityScoreMaxVal", "21");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq2));
	}

	@Test
	public void filterNAmb() {

		/*
		 * NAmb = 0
		 */
		String seq1s1 = "@cluster_2:UMI_ATTCCG";
		String seq1s2 = "TTTCCGGGGCACATAATCTTCAGCCGGGCGC";
		String seq1s4 = "9C;=;=<9@4868>9:67AA<9>65<=>591";
		Sequence seq1 = new Sequence(seq1s1, seq1s2, commLine, seq1s4);

		/*
		 * NAmb = 1
		 */
		String seq2s1 = "@cluster_8:UMI_CTTTGA";
		String seq2s2 = "TATCCUTGCAATANTCTCCGAACGGGAGAG";
		String seq2s4 = "1/04.72,(003,-2-22+00-12./.-.4";
		Sequence seq2 = new Sequence(seq2s1, seq2s2, commLine, seq2s4);

		/*
		 * NAmb = 2
		 */
		String seq3s1 = "@cluster_12:UMI_GGTCAA";
		String seq3s2 = "GCAGTTTTAGATCAATATATANNAGAGCA";
		String seq3s4 = "?7?AEEC@>=1?A?EEEB9ECB?==:B.A";
		Sequence seq3 = new Sequence(seq3s1, seq3s2, commLine, seq3s4);

		JavaRDD<Sequence> original = jsc.parallelize(Arrays.asList(seq1, seq2, seq3));
		JavaRDD<Sequence> emptyRdd = jsc.parallelize(new ArrayList<Sequence>());
		JavaRDD<Sequence> filtered;
		ArrayList<Sequence> list;
		SingleFilter filter = new NAmb();

		// Test for empty RDD
		filtered = filter.validate(emptyRdd);
		assertEquals(filtered.count(), 0);

		ExecutionParametersManager.setParameter("NAmbMinVal", "");
		ExecutionParametersManager.setParameter("NAmbMaxVal", "");
		filtered = filter.validate(original);
		assertEquals(original.collect(), filtered.collect());

		ExecutionParametersManager.setParameter("NAmbMinVal", "1");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("NAmbMinVal", "");
		ExecutionParametersManager.setParameter("NAmbMaxVal", "1");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));

		ExecutionParametersManager.setParameter("NAmbMinVal", "1");
		ExecutionParametersManager.setParameter("NAmbMaxVal", "1");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq2));
	}

	@Test
	public void filterNAmbP() {

		/*
		 * NAmbP = 0
		 */
		String seq1s1 = "@cluster_2:UMI_ATTCCG";
		String seq1s2 = "TTTCCGGGGCACATAATCTTCAGCCGGGCGC";
		String seq1s4 = "9C;=;=<9@4868>9:67AA<9>65<=>591";
		Sequence seq1 = new Sequence(seq1s1, seq1s2, commLine, seq1s4);

		/*
		 * NAmbP = 0.1
		 */
		String seq2s1 = "@cluster_8:UMI_CTTTGA";
		String seq2s2 = "TATCCUNGCAATANTCTCCGAACNGGAGAG";
		String seq2s4 = "1/04.72,(003,-2-22+00-12./.-.4";
		Sequence seq2 = new Sequence(seq2s1, seq2s2, commLine, seq2s4);

		/*
		 * NAmbP = 0.1724137931034483
		 */
		String seq3s1 = "@cluster_12:UMI_GGTCAA";
		String seq3s2 = "GCAGTTNNAGATCAATATATNNNAGAGCA";
		String seq3s4 = "?7?AEEC@>=1?A?EEEB9ECB?==:B.A";
		Sequence seq3 = new Sequence(seq3s1, seq3s2, commLine, seq3s4);

		JavaRDD<Sequence> original = jsc.parallelize(Arrays.asList(seq1, seq2, seq3));
		JavaRDD<Sequence> emptyRdd = jsc.parallelize(new ArrayList<Sequence>());
		JavaRDD<Sequence> filtered;
		ArrayList<Sequence> list;
		SingleFilter filter = new NAmbP();

		// Test for empty RDD
		filtered = filter.validate(emptyRdd);
		assertEquals(filtered.count(), 0);

		ExecutionParametersManager.setParameter("NAmbPMinVal", "");
		ExecutionParametersManager.setParameter("NAmbPMaxVal", "");
		filtered = filter.validate(original);
		assertEquals(original.collect(), filtered.collect());

		ExecutionParametersManager.setParameter("NAmbPMinVal", "0.05");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("NAmbPMinVal", "");
		ExecutionParametersManager.setParameter("NAmbPMaxVal", "0.1");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));

		ExecutionParametersManager.setParameter("NAmbPMinVal", "0.05");
		ExecutionParametersManager.setParameter("NAmbPMaxVal", "0.1");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq2));
	}

	@Test
	public void filterGC() {

		/*
		 * G = 4 C = 8
		 */
		String seq1s1 = "@cluster_21:UMI_AGAACA";
		String seq1s2 = "GGCATTGCAAAATTTNTTSCACCCCCAG";
		String seq1s4 = ">=2.660/?:36AD;0<14703640334";
		Sequence seq1 = new Sequence(seq1s1, seq1s2, commLine, seq1s4);

		/*
		 * G = 6 C = 7
		 */
		String seq2s1 = "@cluster_8:UMI_CTTTGA";
		String seq2s2 = "TATCCUNGCAATANTCTCCGAACNGGAGAG";
		String seq2s4 = "1/04.72,(003,-2-22+00-12./.-.4";
		Sequence seq2 = new Sequence(seq2s1, seq2s2, commLine, seq2s4);

		/*
		 * G = 8 C = 6
		 */
		String seq3s1 = "@cluster_12:UMI_GGTCAA";
		String seq3s2 = "GCCGTCNCAGATCAATATATNGGGGAGCA";
		String seq3s4 = "?7?AEEC@>=1?A?EEEB9ECB?==:B.A";
		Sequence seq3 = new Sequence(seq3s1, seq3s2, commLine, seq3s4);

		JavaRDD<Sequence> original = jsc.parallelize(Arrays.asList(seq1, seq2, seq3));
		JavaRDD<Sequence> emptyRdd = jsc.parallelize(new ArrayList<Sequence>());
		JavaRDD<Sequence> filtered;
		ArrayList<Sequence> list;
		SingleFilter filter = new GC();

		// Test for empty RDD
		filtered = filter.validate(emptyRdd);
		assertEquals(filtered.count(), 0);

		ExecutionParametersManager.setParameter("GCMinVal", "");
		ExecutionParametersManager.setParameter("GCMaxVal", "");
		filtered = filter.validate(original);
		assertEquals(original.collect(), filtered.collect());

		ExecutionParametersManager.setParameter("GCMinVal", "13");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("GCMinVal", "");
		ExecutionParametersManager.setParameter("GCMaxVal", "13");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));

		ExecutionParametersManager.setParameter("GCMinVal", "13");
		ExecutionParametersManager.setParameter("GCMaxVal", "13");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq2));
	}

	@Test
	public void filterGCP() {

		/*
		 * GCP = 0.42857142857142855
		 */
		String seq1s1 = "@cluster_21:UMI_AGAACA";
		String seq1s2 = "GGCATTGCAAAATTTNTTSCACCCCCAG";
		String seq1s4 = ">=2.660/?:36AD;0<14703640334";
		Sequence seq1 = new Sequence(seq1s1, seq1s2, commLine, seq1s4);

		/*
		 * GCP = 0.43333333333333335
		 */
		String seq2s1 = "@cluster_8:UMI_CTTTGA";
		String seq2s2 = "TATCCUNGCAATANTCTCCGAACNGGAGAG";
		String seq2s4 = "1/04.72,(003,-2-22+00-12./.-.4";
		Sequence seq2 = new Sequence(seq2s1, seq2s2, commLine, seq2s4);

		/*
		 * GCP = 0.4444444444444444
		 */
		String seq3s1 = "@cluster_29:UMI_GCAGGA";
		String seq3s2 = "CCCCCTTAAATAGCTGTTTATTTGGCC";
		String seq3s4 = "8;;;>DC@DAC=B?C@9?B?CDCB@><";
		Sequence seq3 = new Sequence(seq3s1, seq3s2, commLine, seq3s4);

		JavaRDD<Sequence> original = jsc.parallelize(Arrays.asList(seq1, seq2, seq3));
		JavaRDD<Sequence> emptyRdd = jsc.parallelize(new ArrayList<Sequence>());
		JavaRDD<Sequence> filtered;
		ArrayList<Sequence> list;
		SingleFilter filter = new GCP();

		// Test for empty RDD
		filtered = filter.validate(emptyRdd);
		assertEquals(filtered.count(), 0);

		ExecutionParametersManager.setParameter("GCPMinVal", "");
		ExecutionParametersManager.setParameter("GCPMaxVal", "");
		filtered = filter.validate(original);
		assertEquals(original.collect(), filtered.collect());

		ExecutionParametersManager.setParameter("GCPMinVal", "0.43");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("GCPMinVal", "");
		ExecutionParametersManager.setParameter("GCPMaxVal", "0.44");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));

		ExecutionParametersManager.setParameter("GCPMinVal", "0.43");
		ExecutionParametersManager.setParameter("GCPMaxVal", "0.44");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq2));
	}

	@Test
	public void filterNonIupac() {

		/*
		 * U = 0
		 */
		String seq1s1 = "@cluster_2:UMI_ATTCCG";
		String seq1s2 = "TTTCCGGGGCACATAATCTTCAGCCGGGCGC";
		String seq1s4 = "9C;=;=<9@4868>9:67AA<9>65<=>591";
		Sequence seq1 = new Sequence(seq1s1, seq1s2, commLine, seq1s4);

		/*
		 * U = 1
		 */
		String seq2s1 = "@cluster_8:UMI_CTTTGA";
		String seq2s2 = "TATCCUNGCAATANTCTCCGAACNGGAGAG";
		String seq2s4 = "1/04.72,(003,-2-22+00-12./.-.4";
		Sequence seq2 = new Sequence(seq2s1, seq2s2, commLine, seq2s4);

		/*
		 * U = 0
		 */
		String seq3s1 = "@cluster_12:UMI_GGTCAA";
		String seq3s2 = "GCAGTTNNAGATCAATATATNNNAGAGCA";
		String seq3s4 = "?7?AEEC@>=1?A?EEEB9ECB?==:B.A";
		Sequence seq3 = new Sequence(seq3s1, seq3s2, commLine, seq3s4);

		JavaRDD<Sequence> original = jsc.parallelize(Arrays.asList(seq1, seq2, seq3));
		JavaRDD<Sequence> filtered;
		JavaRDD<Sequence> emptyRdd = jsc.parallelize(new ArrayList<Sequence>());
		ArrayList<Sequence> list;
		SingleFilter filter = new NonIupac();

		// Test for empty RDD
		filtered = filter.validate(emptyRdd);
		assertEquals(filtered.count(), 0);

		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq3));
	}

	@Test
	public void filterPattern() {

		/*
		 * AT = 1
		 */
		String seq1s1 = "@cluster_2:UMI_ATTCCG";
		String seq1s2 = "TTTCCGGGGCACATTTTCTTCAGCCGGGCGC";
		String seq1s4 = "9C;=;=<9@4868>9:67AA<9>65<=>591";
		Sequence seq1 = new Sequence(seq1s1, seq1s2, commLine, seq1s4);

		/*
		 * AT = 2
		 */
		String seq2s1 = "@cluster_8:UMI_CTTTGA";
		String seq2s2 = "TATCCUNGCAATATTCTCCGAACNGGAGAG";
		String seq2s4 = "1/04.72,(003,-2-22+00-12./.-.4";
		Sequence seq2 = new Sequence(seq2s1, seq2s2, commLine, seq2s4);

		/*
		 * AT = 3
		 */
		String seq3s1 = "@cluster_12:UMI_GGTCAA";
		String seq3s2 = "GCAGTTNNAGTTATATATTTNNNAGAGCA";
		String seq3s4 = "?7?AEEC@>=1?A?EEEB9ECB?==:B.A";
		Sequence seq3 = new Sequence(seq3s1, seq3s2, commLine, seq3s4);

		JavaRDD<Sequence> original = jsc.parallelize(Arrays.asList(seq1, seq2, seq3));
		JavaRDD<Sequence> emptyRdd = jsc.parallelize(new ArrayList<Sequence>());
		JavaRDD<Sequence> filtered;
		ArrayList<Sequence> list;
		SingleFilter filter = new Pattern();

		// Test for empty RDD
		filtered = filter.validate(emptyRdd);
		assertEquals(filtered.count(), 0);

		ExecutionParametersManager.setParameter("Pattern", "");
		ExecutionParametersManager.setParameter("RepPattern", "");
		filtered = filter.validate(original);
		assertEquals(original.collect(), filtered.collect());

		ExecutionParametersManager.setParameter("Pattern", "AT");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 3);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 3);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("Pattern", "");
		ExecutionParametersManager.setParameter("RepPattern", "3");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 3);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 3);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("Pattern", "AT");
		ExecutionParametersManager.setParameter("RepPattern", "2");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("Pattern", "AT");
		ExecutionParametersManager.setParameter("RepPattern", "3");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq3));
	}

	@Test
	public void filterNoPattern() {
		/*
		 * AT = 1
		 */
		String seq1s1 = "@cluster_2:UMI_ATTCCG";
		String seq1s2 = "TTTCCGGGGCACATTTTCTTCAGCCGGGCGC";
		String seq1s4 = "9C;=;=<9@4868>9:67AA<9>65<=>591";
		Sequence seq1 = new Sequence(seq1s1, seq1s2, commLine, seq1s4);

		/*
		 * AT = 2
		 */
		String seq2s1 = "@cluster_8:UMI_CTTTGA";
		String seq2s2 = "TATCCUNGCAATATTCTCCGAACNGGAGAG";
		String seq2s4 = "1/04.72,(003,-2-22+00-12./.-.4";
		Sequence seq2 = new Sequence(seq2s1, seq2s2, commLine, seq2s4);

		/*
		 * AT = 3
		 */
		String seq3s1 = "@cluster_12:UMI_GGTCAA";
		String seq3s2 = "GCAGTTNNAGTTATATATTTNNNAGAGCA";
		String seq3s4 = "?7?AEEC@>=1?A?EEEB9ECB?==:B.A";
		Sequence seq3 = new Sequence(seq3s1, seq3s2, commLine, seq3s4);

		JavaRDD<Sequence> original = jsc.parallelize(Arrays.asList(seq1, seq2, seq3));
		JavaRDD<Sequence> emptyRdd = jsc.parallelize(new ArrayList<Sequence>());
		JavaRDD<Sequence> filtered;
		ArrayList<Sequence> list;
		SingleFilter filter = new NoPattern();

		// Test for empty RDD
		filtered = filter.validate(emptyRdd);
		assertEquals(filtered.count(), 0);

		ExecutionParametersManager.setParameter("NoPattern", "");
		ExecutionParametersManager.setParameter("RepNoPattern", "");
		filtered = filter.validate(original);
		assertEquals(original.collect(), filtered.collect());

		ExecutionParametersManager.setParameter("NoPattern", "AT");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 3);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 3);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("NoPattern", "");
		ExecutionParametersManager.setParameter("RepNoPattern", "3");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 3);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 3);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("NoPattern", "AT");
		ExecutionParametersManager.setParameter("RepNoPattern", "2");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq1));

		ExecutionParametersManager.setParameter("NoPattern", "AT");
		ExecutionParametersManager.setParameter("RepNoPattern", "3");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));
	}

	@Test
	public void filterBaseN() {
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
		JavaRDD<Sequence> emptyRdd = jsc.parallelize(new ArrayList<Sequence>());
		JavaRDD<Sequence> filtered;
		ArrayList<Sequence> list;
		SingleFilter filter = new BaseN();

		// Test for empty RDD
		filtered = filter.validate(emptyRdd);
		assertEquals(filtered.count(), 0);

		ExecutionParametersManager.setParameter("Base", "");
		ExecutionParametersManager.setParameter("BaseMinVal", "");
		ExecutionParametersManager.setParameter("BaseMaxVal", "");
		filtered = filter.validate(original);
		assertEquals(original.collect(), filtered.collect());

		ExecutionParametersManager.setParameter("Base", "A");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 5);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 5);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));
		assertTrue(list.contains(seq4));
		assertTrue(list.contains(seq5));

		ExecutionParametersManager.setParameter("Base", "");
		ExecutionParametersManager.setParameter("BaseMinVal", "3");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 5);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 5);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));
		assertTrue(list.contains(seq4));
		assertTrue(list.contains(seq5));

		ExecutionParametersManager.setParameter("Base", "");
		ExecutionParametersManager.setParameter("BaseMinVal", "");
		ExecutionParametersManager.setParameter("BaseMaxVal", "3");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 5);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 5);
		assertTrue(list.contains(seq1));
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));
		assertTrue(list.contains(seq4));
		assertTrue(list.contains(seq5));

		ExecutionParametersManager.setParameter("Base", "A");
		ExecutionParametersManager.setParameter("BaseMinVal", "7");
		ExecutionParametersManager.setParameter("BaseMaxVal", "");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 3);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 3);
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));
		assertTrue(list.contains(seq4));

		ExecutionParametersManager.setParameter("Base", "T");
		ExecutionParametersManager.setParameter("BaseMinVal", "");
		ExecutionParametersManager.setParameter("BaseMaxVal", "6");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 2);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 2);
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("Base", "C");
		ExecutionParametersManager.setParameter("BaseMinVal", "5");
		ExecutionParametersManager.setParameter("BaseMaxVal", "8");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 3);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 3);
		assertTrue(list.contains(seq2));
		assertTrue(list.contains(seq4));
		assertTrue(list.contains(seq5));

		ExecutionParametersManager.setParameter("Base", "AT");
		ExecutionParametersManager.setParameter("BaseMinVal", "3");
		ExecutionParametersManager.setParameter("BaseMaxVal", "4");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq3));

		ExecutionParametersManager.setParameter("Base", "A|T|C|AT|G");
		ExecutionParametersManager.setParameter("BaseMinVal", "7|-1|5|2|-1");
		ExecutionParametersManager.setParameter("BaseMaxVal", "-1|6|8|2|-1");
		filtered = filter.validate(original);
		assertEquals(filtered.count(), 1);
		list = new ArrayList<>(filtered.collect());
		assertEquals(list.size(), 1);
		assertTrue(list.contains(seq2));
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
		JavaRDD<Sequence> emptyRdd = jsc.parallelize(new ArrayList<Sequence>());
		JavaRDD<Sequence> filtered;
		ArrayList<Sequence> list;
		SingleFilter filter = new BaseP();

		// Test for empty RDD
		filtered = filter.validate(emptyRdd);
		assertEquals(filtered.count(), 0);

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
