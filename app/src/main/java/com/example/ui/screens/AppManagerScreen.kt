package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppInfo
import com.example.model.MemoryInfo
import com.example.ui.components.tvClickable
import com.example.ui.components.tvFocusable
import com.example.ui.theme.CardBorderNavy
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.DeepBackground
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.SystemViewModel

@Composable
fun AppManagerScreen(
    installedApps: List<AppInfo>,
    selectedCount: Int,
    memoryInfo: MemoryInfo,
    currentSortType: SystemViewModel.AppSortType,
    onSortTypeChange: (SystemViewModel.AppSortType) -> Unit,
    onToggleApp: (AppInfo) -> Unit,
    onHibernate: (AppInfo) -> Unit,
    onUninstallBatch: () -> Unit
) {
    var searchFilter by remember { mutableStateOf("") }
    val filteredApps = remember(installedApps, searchFilter) {
        installedApps.filter {
            it.appLabel.contains(searchFilter, ignoreCase = true) ||
                    it.packageName.contains(searchFilter, ignoreCase = true)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // App Manager Search & Multi-Uninstall Floating panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .tvFocusable(shape = RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                OutlinedTextField(
                    value = searchFilter,
                    onValueChange = { searchFilter = it },
                    placeholder = { Text(stringResource(R.string.search_apps_placeholder), fontSize = 11.sp, color = TextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("app_search_field"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = CardBorderNavy,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = DeepBackground,
                        unfocusedContainerColor = DeepBackground
                    ),
                    singleLine = true,
                    leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp)) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // SORT SELECTOR CHIPS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.sort_by), color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = { onSortTypeChange(SystemViewModel.AppSortType.SIZE) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentSortType == SystemViewModel.AppSortType.SIZE) CyanPrimary else DeepBackground
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .tvFocusable(shape = RoundedCornerShape(8.dp))
                            .testTag("sort_size_button"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        Text(
                            stringResource(R.string.sort_size),
                            color = if (currentSortType == SystemViewModel.AppSortType.SIZE) Color.Black else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = { onSortTypeChange(SystemViewModel.AppSortType.LAST_USED) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentSortType == SystemViewModel.AppSortType.LAST_USED) CyanPrimary else DeepBackground
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .tvFocusable(shape = RoundedCornerShape(8.dp))
                            .testTag("sort_last_used_button"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp)
                    ) {
                        Text(
                            stringResource(R.string.sort_last_used),
                            color = if (currentSortType == SystemViewModel.AppSortType.LAST_USED) Color.Black else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${stringResource(R.string.batch_uninstaller_title)} ($selectedCount/5 SELECTED)",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = stringResource(R.string.batch_uninstaller_desc),
                            color = TextSecondary,
                            fontSize = 8.sp
                        )
                    }

                    Button(
                        onClick = onUninstallBatch,
                        colors = ButtonDefaults.buttonColors(containerColor = CriticalRed),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .tvFocusable(shape = RoundedCornerShape(10.dp))
                            .testTag("batch_uninstall_button"),
                        enabled = selectedCount > 0
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = PureWhite, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = stringResource(R.string.uninstall_action), color = PureWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // List of Active Apps and their Memory/Background status
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredApps) { app ->
                AppRow(
                    app = app,
                    onCheckedChange = { onToggleApp(app) },
                    onHibernateClick = { onHibernate(app) }
                )
            }
        }
    }
}

@Composable
fun AppRow(
    app: AppInfo,
    onCheckedChange: (Boolean) -> Unit,
    onHibernateClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .tvClickable(
                shape = RoundedCornerShape(16.dp),
                onClick = { onCheckedChange(!app.isSelected) }
            )
            .background(SurfaceNavy, RoundedCornerShape(16.dp))
            .border(1.dp, if (app.isSelected) CyanPrimary else CardBorderNavy, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = app.isSelected,
                onCheckedChange = { onCheckedChange(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = CyanPrimary,
                    uncheckedColor = CardBorderNavy,
                    checkmarkColor = Color.Black
                ),
                modifier = Modifier.testTag("app_checkbox_${app.packageName}")
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column {
                Text(
                    text = app.appLabel,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${app.packageName}  •  ${String.format("%.1f", app.sizeBytes.toFloat() / (1024 * 1024))} MB",
                    color = TextSecondary,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            OutlinedButton(
                onClick = onHibernateClick,
                border = BorderStroke(1.dp, CardBorderNavy),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0x1100E5FF)),
                modifier = Modifier
                    .height(28.dp)
                    .tvFocusable(shape = RoundedCornerShape(8.dp))
                    .testTag("force_stop_${app.packageName}")
            ) {
                Text(text = stringResource(R.string.force_stop_action), color = CyanPrimary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
