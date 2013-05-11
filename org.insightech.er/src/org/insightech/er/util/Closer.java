package org.insightech.er.util;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.insightech.er.Activator;

public final class Closer {
	private Closer() {
		// nop.
	}

	public static void close(final Closeable s) {
		if (s != null) {
			try {
				s.close();
			} catch (final IOException e) {
				// nop.
			}
		}
	}

	public static void close(final Connection s) {
		if (s != null) {
			try {
				s.close();
			} catch (final SQLException e) {
				// nop.
			}
		}
	}

	public static void close(final Statement s) {
		if (s != null) {
			try {
				s.close();
			} catch (final SQLException e) {
				// nop.
			}
		}
	}

	public static void close(final ResultSet s) {
		if (s != null) {
			try {
				s.close();
			} catch (final SQLException e) {
				// nop.
			}
		}
	}

	public static void closeWithAlert(final Closeable s) {
		if (s != null) {
			try {
				s.close();
			} catch (final IOException e) {
				Activator.showExceptionDialog(e);
			}
		}
	}

	public static void closeWithAlert(final Connection s) {
		if (s != null) {
			try {
				s.close();
			} catch (final SQLException e) {
				Activator.showExceptionDialog(e);
			}
		}
	}

	public static void closeWithAlert(final Statement s) {
		if (s != null) {
			try {
				s.close();
			} catch (final SQLException e) {
				Activator.showExceptionDialog(e);
			}
		}
	}

	public static void closeWithAlert(final ResultSet s) {
		if (s != null) {
			try {
				s.close();
			} catch (final SQLException e) {
				Activator.showExceptionDialog(e);
			}
		}
	}
}
