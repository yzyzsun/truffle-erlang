package me.yzyzsun.jiro.nodes;

import com.oracle.truffle.api.nodes.Node;
import lombok.Getter;
import me.yzyzsun.jiro.runtime.JiroModule;

public class ModuleNode extends Node {
    @Getter private final JiroModule module;

    public ModuleNode(JiroModule module) {
        this.module = module;
    }
}
