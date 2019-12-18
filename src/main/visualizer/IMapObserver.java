package visualizer;

import mapElements.IMapElement;
import structures.FieldImage;

public interface IMapObserver {
    void onFiledChanged(IMapElement element, FieldImage newType);
}
