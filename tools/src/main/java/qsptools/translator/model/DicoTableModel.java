package qsptools.translator.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.swing.table.AbstractTableModel;
import qsptools.translator.bean.DicoEntry;
import qsptools.translator.bean.DicoEntryWrapper;

/**
 *
 * @author pseudo555
 */
public class DicoTableModel extends AbstractTableModel {
    
    // cache values
    public List<DicoEntry> items = new ArrayList<>();
    
    // Array containing column name
    private final String[] columnsName = new String[2];
    
    // Index of each column
    public static final int I_ORI = 0;
    public static final int I_TRANS = 1;
    
    
    public DicoTableModel() {
        // fill columnsName
        this.columnsName[I_ORI] = "Original text";
        this.columnsName[I_TRANS] = "Translated text";
        
        init();
    }
    
    /**
     * Fill dico with defaut entries.
     */
    private void init() {
        items.add(new DicoEntry("Белье", "underwear"));
        items.add(new DicoEntry("поликлиника", "policlinic"));
        items.add(new DicoEntry("Обувь", "shoes"));
        items.add(new DicoEntry("КорридорУниверситетаКультуры", "Corridor University of Culture"));
        items.add(new DicoEntry("ПриемнаяКомиссияУниверситета", "Admissions Committee of the University"));
        items.add(new DicoEntry("Рынок", "Mall"));
        items.add(new DicoEntry("ОфисА", "Bureau"));
        items.add(new DicoEntry("имя", "forename"));
        items.add(new DicoEntry("общага", "dorm"));
        items.add(new DicoEntry("бар","saloon"));
    }
    
    @Override
    public String getColumnName(final int index) {
        return this.columnsName[index];
    }
    
    @Override
    public int getColumnCount() {
        return this.columnsName.length;
    }
    
    /**
     * Retrieve a specific row from model data
     * @param rowIndex index of the row to retrieve
     * @return Item
     */
    private DicoEntry getItem(final int rowIndex) {
        try {
            return items.get(rowIndex);
        } catch (final ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
    
    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final DicoEntry item = this.getItem(rowIndex);
        if (item == null) {
            return null;
        }
        // according to the column, retrieve the required value
        switch (columnIndex) {
            case I_ORI:
                return item.getOriginal();
            case I_TRANS:
                return item.getTranslated();
            default:
                return "";
        }
    }
    
    @Override
    public Class getColumnClass(final int columnIndex) {
        // Pour chaque colonne, on définit l'objet utilisé.
        // On appliquera un renderer spécial dessus si nécessaire.
        switch (columnIndex) {
            case I_ORI:
            case I_TRANS:
            default:
                return String.class;
        }
    }
    
    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        // Définition des colonnes éditables
        switch (columnIndex) {
            case I_ORI:
            case I_TRANS:
                return true;
            default:
                return false;
        }
    }
    
    @Override
    public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
        // only if isCellEditable return true
        switch (columnIndex) {
            case I_ORI:
                if(value instanceof String) {
                    items.get(rowIndex).setOriginal((String)value);
                }
                break;
            case I_TRANS:
                if(value instanceof String) {
                    items.get(rowIndex).setTranslated((String)value);
                }
                break;
            default:
                break;
        }
    }
    
    @Override
    public int getRowCount() {
        return items.size();
    }
    
    /**
     * Add new emty item in model.
     */
    public void addNewItem() {
        items.add(new DicoEntry());
        fireTableDataChanged();
    }
    
    /**
     * Add all newitems in items list.
     * @param newitems items to be added
     */
    public void addItems(List<DicoEntry> newitems){
        if(newitems != null && ! newitems.isEmpty()){
            items.addAll(newitems);
            fireTableDataChanged();
        }
    }
    
    /**
     * Clear the model contents.
     * @param fireChange <code>true</code> if fireTableDataChanged mus be called
     */
    public void clear(boolean fireChange) {
        items.clear();
        if (fireChange) {
            fireTableDataChanged();
        }
    }

    public Map<String, String> getCustomDico() {
        return new DicoEntryWrapper(items).getMap();
    }

    /**
     * Remove given row index from model.
     * @param modelIdx Index of the row to be deleted
     */
    public void removeRow(int modelIdx) {
        items.remove(modelIdx);
        fireTableDataChanged();
    }
}
