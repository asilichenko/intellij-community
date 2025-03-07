// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.plugins.groovy.regexp;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.JavaRegExpHost;
import org.intellij.lang.regexp.psi.RegExpChar;
import org.intellij.lang.regexp.psi.RegExpElement;
import org.intellij.lang.regexp.psi.RegExpGroup;
import org.intellij.lang.regexp.psi.RegExpNamedGroupRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.config.GroovyConfigUtils;

import java.util.EnumSet;

/**
 * @author Bas Leijdekkers
 */
public final class GroovyRegExpHost extends JavaRegExpHost {

  @Override
  public boolean supportsNamedGroupSyntax(RegExpGroup group) {
    if (group.getType() == RegExpGroup.Type.NAMED_GROUP) {
      final String version = getGroovyVersion(group);
      return version != null && version.compareTo(GroovyConfigUtils.GROOVY2_0) >= 0;
    }
    return false;
  }

  @Override
  public boolean supportsNamedGroupRefSyntax(RegExpNamedGroupRef ref) {
    if (ref.isNamedGroupRef()) {
      final String version = getGroovyVersion(ref);
      return version != null && version.compareTo(GroovyConfigUtils.GROOVY2_0) >= 0;
    }
    return false;
  }

  @Override
  public @NotNull EnumSet<RegExpGroup.Type> getSupportedNamedGroupTypes(RegExpElement context) {
    final String version = getGroovyVersion(context);
    if (version == null || version.compareTo(GroovyConfigUtils.GROOVY2_0) < 0) {
      return EMPTY_NAMED_GROUP_TYPES;
    }
    return SUPPORTED_NAMED_GROUP_TYPES;
  }

  @Override
  public boolean supportsExtendedHexCharacter(RegExpChar regExpChar) {
    final String version = getGroovyVersion(regExpChar);
    return version != null && version.compareTo(GroovyConfigUtils.GROOVY2_0) >= 0;
  }

  private static String getGroovyVersion(PsiElement element) {
    final Module module = ModuleUtilCore.findModuleForPsiElement(element);
    if (module == null) {
      return null;
    }
    return GroovyConfigUtils.getInstance().getSDKVersion(module);
  }
}
