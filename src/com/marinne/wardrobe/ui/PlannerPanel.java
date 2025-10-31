package com.marinne.wardrobe.ui;

import com.marinne.wardrobe.model.Outfit;
import com.marinne.wardrobe.model.PlannerAssignment;
import com.marinne.wardrobe.model.WardrobeState;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Calendar planner that lets Marinne assign outfits to specific dates or events.
 */
public final class PlannerPanel extends JPanel implements WardrobeState.StateListener {

    private final WardrobeState state;
    private final JComboBox<Month> monthSelector = new JComboBox<>(Month.values());
    private final JComboBox<Integer> yearSelector = new JComboBox<>();
    private final PlannerTableModel tableModel = new PlannerTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextArea detailArea = new JTextArea();

    public PlannerPanel(WardrobeState state) {
        super(new BorderLayout(12, 12));
        this.state = state;
        state.registerListener(this);
        initYearSelector();
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCalendar(), BorderLayout.CENTER);
        add(buildDetailPanel(), BorderLayout.EAST);
        refreshCalendar();
    }

    private void initYearSelector() {
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear - 1; year <= currentYear + 2; year++) {
            yearSelector.addItem(year);
        }
        yearSelector.setSelectedItem(currentYear);
        monthSelector.setSelectedItem(LocalDate.now().getMonth());
        monthSelector.addActionListener(e -> refreshCalendar());
        yearSelector.addActionListener(e -> refreshCalendar());
    }

    private Component buildHeader() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.add(new JLabel("Month"));
        panel.add(monthSelector);
        panel.add(new JLabel("Year"));
        panel.add(yearSelector);
        JButton todayButton = new JButton("Jump to Today");
        todayButton.addActionListener(e -> {
            LocalDate today = LocalDate.now();
            monthSelector.setSelectedItem(today.getMonth());
            yearSelector.setSelectedItem(today.getYear());
            highlightDate(today);
        });
        panel.add(todayButton);
        return panel;
    }

    private Component buildCalendar() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new TitledBorder("Outfit Planner"));
        table.setRowHeight(90);
        table.setDefaultRenderer(Object.class, new PlannerCellRenderer());
        table.setCellSelectionEnabled(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                LocalDate date = tableModel.getDateAt(row, col);
                if (date == null) {
                    return;
                }
                if (e.getClickCount() == 2) {
                    openAssignmentDialog(date);
                } else {
                    showDetailsFor(date);
                }
            }
        });
        container.add(new JScrollPane(table), BorderLayout.CENTER);
        return container;
    }

    private Component buildDetailPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(260, 0));
        panel.setBorder(new TitledBorder("Scheduled Outfit"));

        detailArea.setEditable(false);
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));

        JButton clearButton = new JButton("Clear Assignment");
        clearButton.addActionListener(e -> {
            LocalDate selectedDate = tableModel.getSelectedDate();
            if (selectedDate != null) {
                state.assignOutfit(selectedDate, null, "", "");
            }
        });

        panel.add(new JScrollPane(detailArea), BorderLayout.CENTER);
        panel.add(clearButton, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshCalendar() {
        YearMonth month = YearMonth.of((Integer) yearSelector.getSelectedItem(), (Month) monthSelector.getSelectedItem());
        tableModel.setMonth(month, state.getPlanner());
        detailArea.setText("Double-click a day to assign an outfit.");
    }

    private void openAssignmentDialog(LocalDate date) {
        List<Outfit> outfits = state.getOutfits();
        if (outfits.isEmpty()) {
            detailArea.setText("Save at least one outfit before planning.");
            return;
        }

        JComboBox<Outfit> outfitChooser = new JComboBox<>(outfits.toArray(new Outfit[0]));
        outfitChooser.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Outfit outfit) {
                    ((JLabel) comp).setText(outfit.getName());
                }
                return comp;
            }
        });

        JTextField eventField = new JTextField();
        JTextArea notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        Optional<PlannerAssignment> existing = Optional.ofNullable(state.getPlanner().get(date));
        existing.ifPresent(assignment -> {
            state.findOutfit(assignment.getOutfitId()).ifPresent(outfitChooser::setSelectedItem);
            eventField.setText(assignment.getEvent());
            notesArea.setText(assignment.getNotes());
        });

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        form.add(new JLabel("Outfit"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        form.add(outfitChooser, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        form.add(new JLabel("Event / Occasion"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        form.add(eventField, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Notes"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;
        form.add(new JScrollPane(notesArea), gbc);

        int result = JOptionPane.showConfirmDialog(this, form,
                "Plan for " + date.format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Outfit selected = (Outfit) outfitChooser.getSelectedItem();
            if (selected != null) {
                state.assignOutfit(date, selected.getId(), eventField.getText(), notesArea.getText());
                showDetailsFor(date);
            }
        }
    }

    private void showDetailsFor(LocalDate date) {
        tableModel.setSelectedDate(date);
        PlannerAssignment assignment = state.getPlanner().get(date);
        if (assignment == null) {
            detailArea.setText("No outfit assigned yet for " + date + ".");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Date: ").append(date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))).append('\n');
        state.findOutfit(assignment.getOutfitId()).ifPresent(outfit ->
                sb.append("Outfit: ").append(outfit.getName()).append('\n'));
        if (!assignment.getEvent().isBlank()) {
            sb.append("Event: ").append(assignment.getEvent()).append('\n');
        }
        if (!assignment.getNotes().isBlank()) {
            sb.append("Notes:\n").append(assignment.getNotes());
        }
        detailArea.setText(sb.toString());
    }

    private void highlightDate(LocalDate date) {
        refreshCalendar();
        showDetailsFor(date);
    }

    @Override
    public void onItemsChanged() {
        // no-op
    }

    @Override
    public void onOutfitsChanged() {
        // update detail area if necessary
        SwingUtilities.invokeLater(() -> {
            LocalDate selected = tableModel.getSelectedDate();
            if (selected != null) {
                showDetailsFor(selected);
            }
        });
    }

    @Override
    public void onPlannerChanged() {
        SwingUtilities.invokeLater(this::refreshCalendar);
    }

    private final class PlannerTableModel extends AbstractTableModel {

        private final String[] columns = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        private YearMonth month = YearMonth.now();
        private final List<List<LocalDate>> weeks = new ArrayList<>();
        private LocalDate selectedDate;

        void setMonth(YearMonth month, Map<LocalDate, PlannerAssignment> assignments) {
            this.month = month;
            weeks.clear();
            LocalDate first = month.atDay(1);
            LocalDate start = first.minusDays((first.getDayOfWeek().getValue() + 6) % 7);
            LocalDate cursor = start;
            while (cursor.isBefore(month.plusMonths(1).atDay(1)) || weeks.size() < 6) {
                List<LocalDate> week = new ArrayList<>(7);
                for (int i = 0; i < 7; i++) {
                    if (cursor.getMonth() == month.getMonth()) {
                        week.add(cursor);
                    } else {
                        week.add(null);
                    }
                    cursor = cursor.plusDays(1);
                }
                weeks.add(week);
                if (cursor.getMonth() != month.getMonth() && cursor.getDayOfWeek().getValue() == 1) {
                    break;
                }
            }
            fireTableStructureChanged();
        }

        LocalDate getSelectedDate() {
            return selectedDate;
        }

        void setSelectedDate(LocalDate date) {
            this.selectedDate = date;
            table.repaint();
        }

        LocalDate getDateAt(int row, int column) {
            if (row < 0 || column < 0 || row >= weeks.size()) {
                return null;
            }
            return weeks.get(row).get(column);
        }

        @Override
        public int getRowCount() {
            return weeks.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return weeks.get(rowIndex).get(columnIndex);
        }
    }

    private final class PlannerCellRenderer extends DefaultTableCellRenderer {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d");

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(LEFT);
            setVerticalAlignment(TOP);
            setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(220, 210, 200)));

            if (value instanceof LocalDate date) {
                setText(date.format(formatter));
                PlannerAssignment assignment = state.getPlanner().get(date);
                if (assignment != null) {
                    String outfitName = state.findOutfit(assignment.getOutfitId())
                            .map(Outfit::getName)
                            .orElse("(missing look)");
                    StringBuilder sb = new StringBuilder();
                    sb.append("<html><b>").append(date.format(formatter)).append("</b><br><span style='color:#7b6a60'>")
                            .append(outfitName);
                    if (!assignment.getEvent().isBlank()) {
                        sb.append("<br><i>").append(assignment.getEvent()).append("</i>");
                    }
                    sb.append("</span></html>");
                    setText(sb.toString());
                }

                if (date.equals(tableModel.getSelectedDate())) {
                    setBackground(new Color(255, 248, 235));
                } else if (date.getMonth() == monthSelector.getSelectedItem()) {
                    setBackground(Color.WHITE);
                } else {
                    setBackground(new Color(245, 240, 236));
                }
            } else {
                setText("");
                setBackground(new Color(245, 240, 236));
            }
            return this;
        }
    }
}
