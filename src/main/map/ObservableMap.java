package map;

import mapElements.IMapElement;
import visualizer.IMapObserver;

public interface ObservableMap {
    void notifyOnFieldChanged(IMapElement position);
    void addObserver(IMapObserver observer);
    void removeObserver(IMapObserver observer);

}
