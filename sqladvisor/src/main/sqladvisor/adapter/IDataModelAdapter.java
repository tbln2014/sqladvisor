package sqladvisor.adapter;

import org.apache.commons.collections15.Transformer;

import sqladvisor.DataModel;


public interface IDataModelAdapter<T> extends Transformer<T, DataModel> {
 // Marker Interface mit Typspezifizierung
}
