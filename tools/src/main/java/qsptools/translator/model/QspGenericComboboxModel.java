package qsptools.translator.model;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 * Generic combobox model.
 * @author pseudo555
 * @param <E> 
 */
public class QspGenericComboboxModel<E> extends AbstractListModel<E> implements ComboBoxModel<E> {
	private static final long serialVersionUID = 8680463279238612775L;
	private final E[] values;
    private E selection = null;
    
    public QspGenericComboboxModel(E[] values) {
        super();
        this.values = values;
    }
    
    @Override
    public E getElementAt(int index) {
        return values[index];
    }
    
    @Override
    public int getSize() {
        return values.length;
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public void setSelectedItem(Object anItem) {
        selection = (E) anItem;
    }
    
    @Override
    public E getSelectedItem() {
        return selection;
    }
}
