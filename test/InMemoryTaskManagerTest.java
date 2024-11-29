import managers.InMemoryTaskManager;
import managers.Managers;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return Managers.getInMemoryTaskManager();
    }
}