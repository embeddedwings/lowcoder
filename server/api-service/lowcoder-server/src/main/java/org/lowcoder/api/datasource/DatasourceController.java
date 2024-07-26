package org.lowcoder.api.datasource;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.lowcoder.api.framework.view.ResponseView;
import org.lowcoder.api.permission.view.CommonPermissionView;
import org.lowcoder.api.util.BusinessEventPublisher;
import org.lowcoder.api.util.GIDUtil;
import org.lowcoder.domain.datasource.model.Datasource;
import org.lowcoder.domain.datasource.model.DatasourceDO;
import org.lowcoder.domain.datasource.repository.DatasourceDORepository;
import org.lowcoder.domain.datasource.service.DatasourceService;
import org.lowcoder.domain.datasource.service.DatasourceStructureService;
import org.lowcoder.domain.permission.model.ResourceRole;
import org.lowcoder.domain.plugin.client.dto.GetPluginDynamicConfigRequestDTO;
import org.lowcoder.sdk.constants.FieldName;
import org.lowcoder.sdk.exception.BizError;
import org.lowcoder.sdk.models.DatasourceStructure;
import org.lowcoder.sdk.models.DatasourceTestResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.lowcoder.plugin.api.event.LowcoderEvent.EventType.*;
import static org.lowcoder.sdk.exception.BizError.INVALID_PARAMETER;
import static org.lowcoder.sdk.util.ExceptionUtils.ofError;
import static org.lowcoder.sdk.util.LocaleUtils.getLocale;

@RequiredArgsConstructor
@RestController
@Slf4j
public class DatasourceController implements DatasourceEndpoints
{
    private final DatasourceStructureService datasourceStructureService;
    private final DatasourceApiService datasourceApiService;
    private final UpsertDatasourceRequestMapper upsertDatasourceRequestMapper;
    private final BusinessEventPublisher businessEventPublisher;
    private final DatasourceService datasourceService;
    private final GIDUtil gidUtil;

    @Override
	public Mono<ResponseView<Datasource>> create(@Valid @RequestBody UpsertDatasourceRequest request) {
        return datasourceApiService.create(upsertDatasourceRequestMapper.resolve(request))
                .delayUntil(datasourceService::removePasswordTypeKeysFromJsDatasourcePluginConfig)
                .delayUntil(datasource -> businessEventPublisher.publishDatasourceEvent(datasource, DATA_SOURCE_CREATE))
                .map(ResponseView::success);
    }

    @Override
    public Mono<ResponseView<Datasource>> getById(@PathVariable String id) {
        String objectId = gidUtil.convertDatasourceIdToObjectId(id);
        return datasourceApiService.findByIdWithPermission(objectId)
                .delayUntil(datasourceService::removePasswordTypeKeysFromJsDatasourcePluginConfig)
                .map(ResponseView::success);
    }

    @Override
    public Mono<ResponseView<Datasource>> update(@PathVariable String id,
            @RequestBody UpsertDatasourceRequest request) {
        String objectId = gidUtil.convertDatasourceIdToObjectId(id);
        Datasource resolvedDatasource = upsertDatasourceRequestMapper.resolve(request);
        return datasourceApiService.update(objectId, resolvedDatasource)
                .delayUntil(datasourceService::removePasswordTypeKeysFromJsDatasourcePluginConfig)
                .delayUntil(datasource -> businessEventPublisher.publishDatasourceEvent(datasource, DATA_SOURCE_UPDATE))
                .map(ResponseView::success);
    }

    @Override
    public Mono<ResponseView<Boolean>> delete(@PathVariable String id) {
        String objectId = gidUtil.convertDatasourceIdToObjectId(id);
        return datasourceApiService.delete(objectId)
                .delayUntil(result -> {
                    if (BooleanUtils.isTrue(result)) {
                        return businessEventPublisher.publishDatasourceEvent(objectId, DATA_SOURCE_DELETE);
                    }
                    return Mono.empty();
                })
                .map(ResponseView::success);
    }

    @Override
    public Mono<ResponseView<Boolean>> testDatasource(@RequestBody UpsertDatasourceRequest request) {
        Datasource resolvedDatasource = upsertDatasourceRequestMapper.resolve(request);
        return Mono.deferContextual(ctx -> {
            Locale locale = getLocale(ctx);
            return datasourceApiService.testDatasource(resolvedDatasource)
                    .map(datasourceTestResult -> toResponseView(datasourceTestResult, locale));
        });
    }

    private ResponseView<Boolean> toResponseView(DatasourceTestResult datasourceTestResult, Locale locale) {
        if (datasourceTestResult.isSuccess()) {
            return ResponseView.success(true);
        }
        return ResponseView.error(500, datasourceTestResult.getInvalidMessage(locale));
    }

    @Override
    public Mono<ResponseView<DatasourceStructure>> getStructure(@PathVariable String datasourceId,
            @RequestParam(required = false, defaultValue = "false") boolean ignoreCache) {
        String objectId = gidUtil.convertDatasourceIdToObjectId(datasourceId);
        return datasourceStructureService.getStructure(objectId, ignoreCache)
                .map(ResponseView::success);
    }

    /**
     * Returns the information of all the js data source plugins by the org id which we get by the applicationId, including the data source id,
     * name, type... and the plugin definition of it, excluding the detail configs such as the connection uri, password...
     */
    @Override
    public Mono<ResponseView<List<Datasource>>> listJsDatasourcePlugins(@RequestParam("appId") String applicationId) {
        String objectId = gidUtil.convertApplicationIdToObjectId(applicationId);
        return datasourceApiService.listJsDatasourcePlugins(objectId)
                .collectList()
                .map(ResponseView::success);
    }

    /**
     * Proxy the request to the node service, besides, add the "extra" information from the data source config stored in the mongodb if exists to
     * the request dto. And then return the response from the node service.
     */
    @Override
    public Mono<ResponseView<List<Object>>> getPluginDynamicConfig(
            @RequestBody List<GetPluginDynamicConfigRequestDTO> getPluginDynamicConfigRequestDTOS) {
        if (CollectionUtils.isEmpty(getPluginDynamicConfigRequestDTOS)) {
            return Mono.just(ResponseView.success(Collections.emptyList()));
        }
        return datasourceApiService.getPluginDynamicConfig(getPluginDynamicConfigRequestDTOS)
                .map(ResponseView::success);
    }

    @SneakyThrows
    @Override
    public Mono<ResponseView<List<DatasourceView>>> listOrgDataSources(@RequestParam(name = "orgId") String orgId) {
        if (StringUtils.isBlank(orgId)) {
            return ofError(BizError.INVALID_PARAMETER, "ORG_ID_EMPTY");
        }
        String objectId = gidUtil.convertOrganizationIdToObjectId(orgId);
        return datasourceApiService.listOrgDataSources(objectId)
                .collectList()
                .map(ResponseView::success);
    }

    @Override
    public Mono<ResponseView<List<DatasourceView>>> listAppDataSources(@RequestParam(name = "appId") String applicationId) {
        if (StringUtils.isBlank(applicationId)) {
            return ofError(BizError.INVALID_PARAMETER, "INVALID_APP_ID");
        }
        String objectId = gidUtil.convertApplicationIdToObjectId(applicationId);

        return datasourceApiService.listAppDataSources(objectId)
                .collectList()
                .map(ResponseView::success);
    }

    @Override
    public Mono<ResponseView<CommonPermissionView>> getPermissions(@PathVariable("datasourceId") String datasourceId) {
        String objectId = gidUtil.convertDatasourceIdToObjectId(datasourceId);
        return datasourceApiService.getPermissions(objectId)
                .map(ResponseView::success);
    }

    @Override
    public Mono<ResponseView<Boolean>> grantPermission(@PathVariable String datasourceId,
            @RequestBody BatchAddPermissionRequest request) {
        String objectId = gidUtil.convertDatasourceIdToObjectId(datasourceId);
        ResourceRole role = ResourceRole.fromValue(request.role());
        if (role == null) {
            return ofError(INVALID_PARAMETER, "INVALID_PARAMETER", request.role());
        }
        return datasourceApiService.grantPermission(objectId, request.userIds(), request.groupIds(), role)
                .delayUntil(result -> {
                    if (BooleanUtils.isTrue(result)) {
                        return businessEventPublisher.publishDatasourcePermissionEvent(objectId, request.userIds(),
                                request.groupIds(), request.role(), DATA_SOURCE_PERMISSION_GRANT);
                    }
                    return Mono.empty();
                })
                .map(ResponseView::success);
    }

    @Override
    public Mono<ResponseView<Boolean>> updatePermission(@PathVariable("permissionId") String permissionId,
            @RequestBody UpdatePermissionRequest request) {
        if (request.getResourceRole() == null) {
            return ofError(INVALID_PARAMETER, "INVALID_PARAMETER", request.role());
        }
        return datasourceApiService.updatePermission(permissionId, request.getResourceRole())
                .delayUntil(result -> {
                    if (BooleanUtils.isTrue(result)) {
                        return businessEventPublisher.publishDatasourcePermissionEvent(permissionId, DATA_SOURCE_PERMISSION_UPDATE);
                    }
                    return Mono.empty();
                })
                .map(ResponseView::success);
    }

    @Override
    public Mono<ResponseView<Boolean>> deletePermission(@PathVariable("permissionId") String permissionId) {
        return businessEventPublisher.publishDatasourcePermissionEvent(permissionId, DATA_SOURCE_PERMISSION_DELETE)
                .then(datasourceApiService.deletePermission(permissionId))
                .map(ResponseView::success);
    }

    @Override
    public Mono<ResponseView<Object>> info(@RequestParam(required = false) String datasourceId) {
        String objectId = gidUtil.convertDatasourceIdToObjectId(datasourceId);
        return Mono.just(ResponseView.success(datasourceApiService.info(objectId)));
    }

}
