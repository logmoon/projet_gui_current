package projet_gui.Controllers;

public abstract class ControllerBaseWithSidebar extends ControllerBase {
    @Override
    protected boolean useSidebar() {
        return true;
    }
}
