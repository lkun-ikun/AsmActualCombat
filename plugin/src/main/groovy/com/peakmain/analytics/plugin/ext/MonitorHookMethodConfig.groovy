package com.peakmain.analytics.plugin.ext

import com.peakmain.analytics.plugin.entity.MethodCalledBean
import com.peakmain.analytics.plugin.utils.MethodFieldUtils

/**
 * author ：Peakmain
 * createTime：2022/4/2
 * mail:2726449200@qq.com
 * describe：
 */
class MonitorHookMethodConfig {
    public final static HashMap<String, MethodCalledBean> methodCalledBeans = new HashMap<>()
    static {
        /**
         * 获取设备id
         */
        HashMap<String, String> deviceIdMap = new HashMap<>()
        deviceIdMap.put(MethodFieldUtils.EMPTY_STRING_DESC, MethodFieldUtils.NEW_EMPTY_STRING_TELEPHONY_DESC)
        deviceIdMap.put(MethodFieldUtils.INT_STRING_DESC, MethodFieldUtils.NEW_INT_STRING_TELEPHONY_DESC)
        MethodCalledBean deviceIds = new MethodCalledBean(
                MethodFieldUtils.TELEPHONY_MANAGER_CLASS,
                MethodFieldUtils.GET_DEVICE_ID_METHOD_NAME,
                MethodFieldUtils.EMPTY_INT_RETURN_STRING_DESC,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_DEVICE_ID_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                deviceIdMap)
        addMethodCalledBean(deviceIds)
        /**
         *  获取getMeid
         */
        HashMap<String, String> meIdMap = new HashMap<>()
        meIdMap.put(MethodFieldUtils.EMPTY_STRING_DESC, MethodFieldUtils.NEW_EMPTY_STRING_TELEPHONY_DESC)
        meIdMap.put(MethodFieldUtils.INT_STRING_DESC, MethodFieldUtils.NEW_INT_STRING_TELEPHONY_DESC)
        MethodCalledBean meIds = new MethodCalledBean(
                MethodFieldUtils.TELEPHONY_MANAGER_CLASS,
                MethodFieldUtils.GET_MEID_METHOD_NAME,
                MethodFieldUtils.EMPTY_INT_RETURN_STRING_DESC,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_MEID_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                meIdMap
        )
        addMethodCalledBean(meIds)
        /**
         * 获取connectionInfo
         */
        HashMap<String, String> connectionInfoMap = new HashMap<>()
        connectionInfoMap.put(MethodFieldUtils.EMPTY_WIFI_INFO_DESC, MethodFieldUtils.NEW_WIFI_INFO_WIFI_INFO_DESC)
        MethodCalledBean connectionInfo = new MethodCalledBean(
                MethodFieldUtils.WIFI_MANAGER_CLASS,
                MethodFieldUtils.GET_CONNECTION_INFO_METHOD_NAME,
                MethodFieldUtils.EMPTY_RETURN_WIFI_INFO_DESC,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_CONNECTION_INFO_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                connectionInfoMap
        )
        addMethodCalledBean(connectionInfo)
        /**
         * 获取Mac
         */
        HashMap<String, String> macAddressMap = new HashMap<>()
        macAddressMap.put(MethodFieldUtils.EMPTY_STRING_DESC, MethodFieldUtils.NEW_STRING_WIFI_INFO_DESC)
        MethodCalledBean macAddress = new MethodCalledBean(
                MethodFieldUtils.WIFI_INFO_CLASS,
                MethodFieldUtils.GET_MAC_ADDRESS_METHOD_NAME,
                MethodFieldUtils.EMPTY_RETURN_STRING_DESC,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_MAC_ADDRESS_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                macAddressMap
        )
        addMethodCalledBean(macAddress)
        /**
         * IP地址
         */
        HashMap<String, String> ipAddressMap = new HashMap<>()
        ipAddressMap.put(MethodFieldUtils.EMPTY_INT_DESC, MethodFieldUtils.NEW_INT_WIFI_INFO_DESC)
        MethodCalledBean ipAddress = new MethodCalledBean(
                MethodFieldUtils.WIFI_INFO_CLASS,
                MethodFieldUtils.GET_IP_ADDRESS_METHOD_NAME,
                MethodFieldUtils.EMPTY_RETURN_INT_DESC,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_IP_ADDRESS_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                ipAddressMap
        )
        addMethodCalledBean(ipAddress)
        /**
         * 获取AndroidId
         */
        HashMap<String, String> androidIdMap = new HashMap<>()
        androidIdMap.put(MethodFieldUtils.CONTENTRESOLVER_STRING_DESC, MethodFieldUtils.CONTENTRESOLVER_STRING_DESC)
        MethodCalledBean androidId = new MethodCalledBean(
                MethodFieldUtils.SETTINGS_SECURE_CLASS,
                MethodFieldUtils.GET_STRING_METHOD_NAME,
                MethodFieldUtils.CONTENTRESOLVER_STRING_RETURN_STRING_DESC,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_STRING_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                androidIdMap
        )
        addMethodCalledBean(androidId)
        /**
         * 获取AndroidId2
         */
        HashMap<String, String> androidIdMap2 = new HashMap<>()
        androidIdMap2.put(MethodFieldUtils.CONTENTRESOLVER_STRING_DESC, MethodFieldUtils.CONTENTRESOLVER_STRING_DESC)
        MethodCalledBean androidId2 = new MethodCalledBean(
                MethodFieldUtils.SETTINGS_SECURE_CLASS2,
                MethodFieldUtils.GET_STRING_METHOD_NAME,
                MethodFieldUtils.CONTENTRESOLVER_STRING_RETURN_STRING_DESC,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_STRING_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                androidIdMap2
        )
        addMethodCalledBean(androidId2)

        /**
         * storage
         */
        HashMap<String, String> storage = new HashMap<>()
        storage.put(MethodFieldUtils.FILE_DESC, MethodFieldUtils.FILE_DESC)
        MethodCalledBean storage1 = new MethodCalledBean(
                MethodFieldUtils.STORAGE_CLASS,
                MethodFieldUtils.GET_STORAGE_METHOD_NAME,
                MethodFieldUtils.FILE_RETURN,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_STORAGE_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                storage
        )
        addMethodCalledBean(storage1)
        /**
         * permission
         */
        HashMap<String, String> permissionMap = new HashMap<>()
        permissionMap.put(MethodFieldUtils.REQUEST_PERMISSION_DES, MethodFieldUtils.REQUEST_PERMISSION_DES)
        MethodCalledBean permisison = new MethodCalledBean(
                MethodFieldUtils.PERMISSION_CLASS,
                MethodFieldUtils.GET_PERMISSION_METHOD_NAME,
                MethodFieldUtils.EMPTY_VOID_RETURN_STRING_DESC1,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_PERMISSION_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                permissionMap
        )
        addMethodCalledBean(permisison)
        /**
         * sensorMap
         */
        HashMap<String, String> sensorMap = new HashMap<>()
        sensorMap.put(MethodFieldUtils.SENSOR_DES, MethodFieldUtils.SENSOR_DES)
//        sensorMap.put(MethodFieldUtils.SENSOR_DES2, MethodFieldUtils.SENSOR_DES2)
//        sensorMap.put(MethodFieldUtils.SENSOR_DES3, MethodFieldUtils.SENSOR_DES3)
//        sensorMap.put(MethodFieldUtils.SENSOR_DES4, MethodFieldUtils.SENSOR_DES4)
//        sensorMap.put(MethodFieldUtils.SENSOR_DES5, MethodFieldUtils.SENSOR_DES5)
        MethodCalledBean sensor = new MethodCalledBean(
                MethodFieldUtils.SENSOR_CLASS,
                MethodFieldUtils.GET_SENSORS_METHOD_NAME,
                MethodFieldUtils.SENSOR_RETRUN_DES,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_SENSORS_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                sensorMap
        )
        addMethodCalledBean(sensor)


//        /**
//         * permission
//         */
//        HashMap<String, String> permissionMap2 = new HashMap<>()
//        permissionMap2.put(MethodFieldUtils.REQUEST_PERMISSION_DES2, MethodFieldUtils.REQUEST_PERMISSION_DES2)
//        MethodCalledBean permisison2 = new MethodCalledBean(
//                MethodFieldUtils.PERMISSION_CLASS2,
//                MethodFieldUtils.GET_PERMISSION_METHOD_NAME,
//                MethodFieldUtils.EMPTY_VOID_RETURN_STRING_DESC2,
//                MethodFieldUtils.NEW_METHOD_OWNER,
//                MethodFieldUtils.GET_PERMISSION_METHOD_NAME,
//                MethodFieldUtils.STATIC_OPCODE,
//                permissionMap2
//        )
//        addMethodCalledBean(permisison2)
        /**
         * permission
         */
        HashMap<String, String> permissionMap3 = new HashMap<>()
        permissionMap3.put(MethodFieldUtils.REQUEST_PERMISSION_DES, MethodFieldUtils.REQUEST_PERMISSION_DES)
        MethodCalledBean permisison3 = new MethodCalledBean(
                MethodFieldUtils.PERMISSION_CLASS3,
                MethodFieldUtils.GET_PERMISSION_METHOD_NAME,
                MethodFieldUtils.EMPTY_VOID_RETURN_STRING_DESC1,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_PERMISSION_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                permissionMap3
        )
        addMethodCalledBean(permisison3)

        HashMap<String, String> scanResultMap = new HashMap<>()
        scanResultMap.put(MethodFieldUtils.EMPTY_LIST_DESC, MethodFieldUtils.NEW_EMPTY_LIST_DESC)
        MethodCalledBean scanResults = new MethodCalledBean(
                MethodFieldUtils.WIFI_MANAGER_CLASS,
                MethodFieldUtils.GET_SCAN_RESULTS_METHOD_NAME,
                MethodFieldUtils.EMPTY_RETURN_LIST,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_SCAN_RESULTS_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                scanResultMap
        )
        addMethodCalledBean(scanResults)

        /**
         * 获取Imei
         */
        HashMap<String, String> imeiMap = new HashMap<>()
        imeiMap.put(MethodFieldUtils.EMPTY_STRING_DESC, MethodFieldUtils.NEW_EMPTY_STRING_TELEPHONY_DESC)
        imeiMap.put(MethodFieldUtils.INT_STRING_DESC, MethodFieldUtils.NEW_INT_STRING_TELEPHONY_DESC)
        MethodCalledBean imeiBean = new MethodCalledBean(
                MethodFieldUtils.TELEPHONY_MANAGER_CLASS,
                MethodFieldUtils.GET_IMEI_METHOD_NAME,
                MethodFieldUtils.EMPTY_INT_RETURN_STRING_DESC,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_IMEI_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                imeiMap
        )
        addMethodCalledBean(imeiBean)

        /**
         * 获取subscriberId
         */
        HashMap<String, String> subscriberIdMap = new HashMap<>()
        subscriberIdMap.put(MethodFieldUtils.EMPTY_STRING_DESC, MethodFieldUtils.NEW_EMPTY_STRING_TELEPHONY_DESC)
        subscriberIdMap.put(MethodFieldUtils.INT_STRING_DESC, MethodFieldUtils.NEW_INT_STRING_TELEPHONY_DESC)
        MethodCalledBean subscriberId = new MethodCalledBean(
                MethodFieldUtils.TELEPHONY_MANAGER_CLASS,
                MethodFieldUtils.GET_SUBSCRIBER_ID_METHOD_NAME,
                MethodFieldUtils.EMPTY_INT_RETURN_STRING_DESC,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_SUBSCRIBER_ID_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                subscriberIdMap
        )
        addMethodCalledBean(subscriberId)
        /**
         * 获取simSerialNumber
         */
        HashMap<String, String> simSerialNumberMap = new HashMap<>()
        simSerialNumberMap.put(MethodFieldUtils.EMPTY_STRING_DESC, MethodFieldUtils.NEW_EMPTY_STRING_TELEPHONY_DESC)
        simSerialNumberMap.put(MethodFieldUtils.INT_STRING_DESC, MethodFieldUtils.NEW_INT_STRING_TELEPHONY_DESC)
        MethodCalledBean simSerialNumber = new MethodCalledBean(
                MethodFieldUtils.TELEPHONY_MANAGER_CLASS,
                MethodFieldUtils.GET_SIM_SERIAL_NUMBER_METHOD_NAME,
                MethodFieldUtils.EMPTY_INT_RETURN_STRING_DESC,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.GET_SIM_SERIAL_NUMBER_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                simSerialNumberMap
        )
        addMethodCalledBean(simSerialNumber)


        HashMap<String, String> routerFragment = new HashMap<>()
        routerFragment.put(MethodFieldUtils.ROUTER_DESC, MethodFieldUtils.ROUTER_DESC)
        MethodCalledBean router = new MethodCalledBean(
                MethodFieldUtils.ROUTER_CLASS,
                MethodFieldUtils.WITH_METHOD_NAME,
                MethodFieldUtils.ROUTER_RETURN_DES,
                MethodFieldUtils.NEW_METHOD_OWNER,
                MethodFieldUtils.WITH_METHOD_NAME,
                MethodFieldUtils.STATIC_OPCODE,
                routerFragment
        )
        addMethodCalledBean(router)
    }

    static void addMethodCalledBean(MethodCalledBean methodCalledBean) {
        for (String desc : methodCalledBean.getMethodDescriptor()) {
            print("msg : " + methodCalledBean.getMethodOwner() + methodCalledBean.getMethodName() + desc + "\n")
            methodCalledBeans.put(methodCalledBean.getMethodOwner() + methodCalledBean.getMethodName() + desc, methodCalledBean)
        }
    }

}