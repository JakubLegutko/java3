import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * <ul>
 * CHANGELOG
 * <li>0.70 - refaktoryzacja, dodano prezentacje metody i linii, z ktorej
 * przekazywana jest informacja
 * <li>0.71 - getMethodNameAndLineNumber przeszukuje klasy testowe i pokazuje
 * uproszczony stos wywolania
 * <li>0.72 - formatowanie elementu stosu w osobnej metodzie
 * </ul>
 * 
 * @author oramus
 * @version 0.72
 */
public class PMO_CommonErrorLog {
	private static boolean stateOK = true;
	private static boolean criticalMistake = false;
	private static Queue<String> errorLog = new LinkedList<>();

	synchronized public static boolean isStateOK() {
		return stateOK;
	}

	private static String formatStackElement(StackTraceElement e) {
		if (e == null)
			return "";
		return e.getClassName() + "/" + e.getMethodName() + "/" + e.getLineNumber();
	}

	public static String getMethodNameAndLineNumber() {

		StackTraceElement[] stackElements = Thread.currentThread().getStackTrace();

		StackTraceElement previous = null;
		for (StackTraceElement element : stackElements) {
			if (PMO_Consts.testClasses.contains(element.getClassName())) {
				return formatStackElement(previous) + "<-" + formatStackElement(element);
			}
			previous = element;
		}

		return formatStackElement(stackElements[2]);
	}

	synchronized public static boolean isCriticalMistake() {
		return criticalMistake;
	}

	private static String getCommonPrefix(String start) {
		StringBuilder sb = new StringBuilder();
		sb.append(start);
		sb.append(" ");
		sb.append(PMO_TimeHelper.getTimeFromStart());
		sb.append(" ");
		sb.append(Thread.currentThread().getName());
		sb.append("@");
		sb.append(getMethodNameAndLineNumber());
		sb.append("> ");
		return sb.toString();
	}

	synchronized public static void error(String description) {
		stateOK = false;
		showAndLog("* BLAD *", description);
	}

	private static String addSpace(int length) {
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++)
			sb.append(".");
		return sb.toString();
	}

	private static void showAndLog(String prefix, String description) {
		String txt = getCommonPrefix(prefix) + "\n" + addSpace(prefix.length()) + " " + description;

		errorLog.add(txt);
		PMO_Log.logFormatted(txt);
	}

	synchronized public static void warning(String description) {
		showAndLog("* UWAGA *", description);
	}

	synchronized public static List<String> getErrorLog(int size) {
		return new ArrayList<>(errorLog);
	}

	synchronized public static void criticalMistake() {
		criticalMistake = true;
		PMO_SystemOutRedirect.println("              ---- --- ------ --- ----");
		PMO_SystemOutRedirect.println("              ---- BLAD KRYTYCZNY ----");
		PMO_SystemOutRedirect.println("              ---- BLAD KRYTYCZNY ----");
		PMO_SystemOutRedirect.println("              ---- --- ------ --- ----");
		PMO_SystemOutRedirect.println("Zgłoszono błąd krytyczny, kontynuacja testu nie ma sensu");
		PMO_SystemOutRedirect.println("Poniżej pojawi się log błędów po jego wyświtleniu podjęta");
		PMO_SystemOutRedirect.println("zostanie próba wyłącznia JVM.");

		PMO_SystemOutRedirect.returnToStandardStream();
		errorLog.forEach(java.lang.System.out::println);

		PMO_Log.showLog();
		
		closeJVM();
	}

	private static void closeJVM() {
		java.lang.System.out.println("EXIT");
		java.lang.System.exit(0);
		java.lang.System.out.println("HALT");
		Runtime.getRuntime().halt(0);
	}

}
