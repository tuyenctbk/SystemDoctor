package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.LargeFile
import com.example.model.StorageInfo
import com.example.ui.components.tvFocusable
import com.example.ui.theme.CardBorderNavy
import com.example.ui.theme.CriticalRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.HealthyGreen
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StorageHunterScreen(
    storageInfo: StorageInfo,
    largeFiles: List<LargeFile>,
    onPurgeCache: () -> Unit,
    onDeleteFile: (LargeFile) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero visual bar breakdown
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .tvFocusable(shape = RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.visual_storage_map),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Multi-segmented bar
                    val total = storageInfo.totalBytes.toFloat()
                    val videoPct = storageInfo.videoBytes / total
                    val apkPct = storageInfo.apkBytes / total
                    val cachePct = storageInfo.cacheBytes / total
                    val ghostPct = storageInfo.ghostBytes / total
                    val freePct = storageInfo.freeBytes / total

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(18.dp)
                            .clip(CircleShape)
                            .background(CardBorderNavy)
                    ) {
                        if (videoPct > 0) Box(
                            modifier = Modifier
                                .weight(videoPct)
                                .fillMaxHeight()
                                .background(Color(0xFF3F51B5))
                        )
                        if (apkPct > 0) Box(
                            modifier = Modifier
                                .weight(apkPct)
                                .fillMaxHeight()
                                .background(Color(0xFFFF9800))
                        )
                        if (cachePct > 0) Box(
                            modifier = Modifier
                                .weight(cachePct)
                                .fillMaxHeight()
                                .background(CyanPrimary)
                        )
                        if (ghostPct > 0) Box(
                            modifier = Modifier
                                .weight(ghostPct)
                                .fillMaxHeight()
                                .background(CriticalRed)
                        )
                        if (freePct > 0) Box(
                            modifier = Modifier
                                .weight(freePct)
                                .fillMaxHeight()
                                .background(HealthyGreen)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Legends
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StorageLegendItem(color = Color(0xFF3F51B5), name = stringResource(R.string.legend_video))
                        StorageLegendItem(color = Color(0xFFFF9800), name = stringResource(R.string.legend_apk))
                        StorageLegendItem(color = CyanPrimary, name = stringResource(R.string.legend_cache))
                        StorageLegendItem(color = CriticalRed, name = stringResource(R.string.legend_ghost))
                        StorageLegendItem(color = HealthyGreen, name = stringResource(R.string.legend_free))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onPurgeCache,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .tvFocusable(shape = RoundedCornerShape(12.dp))
                            .testTag("storage_clear_cache_button")
                    ) {
                        Icon(imageVector = Icons.Default.AutoMode, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${stringResource(R.string.clear_cache_ghost)} (${String.format("%.1f", (storageInfo.cacheBytes + storageInfo.ghostBytes).toFloat() / (1024 * 1024 * 1024))} GB)",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Section: "Big File" Hunter (1GB+ elements)
        item {
            Text(
                text = stringResource(R.string.big_file_hunter_title),
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        if (largeFiles.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceNavy.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.no_large_files),
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        } else {
            items(largeFiles) { file ->
                LargeFileRow(file = file, onDelete = { onDeleteFile(file) })
            }
        }
    }
}

@Composable
fun StorageLegendItem(color: Color, name: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(text = name, color = TextSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LargeFileRow(file: LargeFile, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .tvFocusable(shape = RoundedCornerShape(16.dp))
            .background(SurfaceNavy, RoundedCornerShape(16.dp))
            .border(1.dp, CardBorderNavy, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF262E3F), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (file.fileType) {
                        "Video" -> Icons.Default.PlayCircle
                        "APK" -> Icons.Default.InstallMobile
                        "Cache" -> Icons.Default.FolderOpen
                        else -> Icons.Default.InsertDriveFile
                    },
                    contentDescription = null,
                    tint = if (file.fileType == "APK") WarningAmber else CyanPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = file.fileName,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${file.filePath}  •  ${file.fileType}",
                    color = TextSecondary,
                    fontSize = 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = String.format("%.2f GB", file.sizeBytes.toFloat() / (1024 * 1024 * 1024)),
                color = CriticalRed,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .background(Color(0x22FF1744), CircleShape)
                    .size(32.dp)
                    .tvFocusable(shape = CircleShape)
                    .testTag("delete_file_${file.fileName}")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete large file",
                    tint = CriticalRed,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
