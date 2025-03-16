<html lang="en">
<head>
    <meta charset="UTF-8"></meta>
    <meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0"></meta>
    <meta http-equiv="X-UA-Compatible" content="ie=edge"></meta>
    <title>Acuse del informe mensual</title>
    <style type="text/css">
        @page {
            size: a4;
            margin: 1px;
            @bottom-center {
                content: 'Pagina ' counter(page) ' de ' counter(pages);
            }
        }

        p {
            margin: 0;
            padding: 0;
        }

        html {
            width: 100%;
            font-family: Arial, Helvetica, sans-serif;
            font-size: 12px;
        }

        body {
            width: 100%;
        }

        .col-50 {
            width: 50%;
            margin: 0;
            float: left;
        }

        .col-48 {
            width: 48%;
            margin: 0;
            float: left;
        }

        .col-25 {
            width: 25%;
            margin: 0;
            float: left;
        }

        .col-75 {
            width: 75%;
            margin: 0;
            float: left;
        }

        .col-80 {
            width: 80%;
            margin: 0;
            float: left;
        }

        .col-18 {
            width: 18%;
            margin: 0;
            float: left;
        }

        .col-100 {
            width: 100%;
            margin: 0;
            float: left;
        }

        .col-98 {
            width: 98%;
            margin: 0;
            float: left;
        }

        .row {
            width: 100%;
            min-height: 20px;
            float: left;
        }

        page-after {
            page-break-after: always;
        }

        page-before {
            page-break-before: always;
        }

        spacer {
            page-break-before: always;
            display: block;
        }

        table {
            width: 100%;
            border: 1px solid;
            border-collapse: collapse;
        }

        th, td {
            border: 1px solid;
        }

        .text-center {
            text-align: center;
        }

        .gray-header {
            background: #999;
            color: #FFF;
        }

        .graysi-header {
            background: #CCC;
            color: #000;
        }
    </style>
</head>
<body class="">
<div class="row">
    <div class="col-98">
        <a href="https://postdrop.io"><img src="https://pelisat.com.mx/logo-ajustado.png" height="60" alt="Postdrop"></img></a>
    </div>
</div>
<div class="row">
    <div class="col-98">
        <b>DIRECCION DE LOS SERVICIOS DE SEGURIDAD PRIVADA DEL SECRETARIADO EJECUTIVO DEL CONSEJO ESTATAL DE SEGURIDAD PUBLICA DEL ESTADO DE JALISCO:</b>
    </div>
</div>
<div class="row">
    <div class="col-98">
        <p style="text-align: justify">El presente se emite como acuse de recibo del informe mensual digital que corresponde al estado de fuerza presentado por el prestador de servicios de seguridad privada en la plataforma denominada Sistema Integral de Empresas de Seguridad Privada "SIESP", en el cual se hace constar la siguiente informacion:</p>
    </div>
</div>
<div class="row">
    <div class="col-98">
        <br></br>
        <p style="text-align: justify;">Nombre del prestador de servicios:</p>
        <p style="text-align: justify;"><b>${nombrePrestadorServicio}</b></p>
        <br></br>
        <p style="text-align: justify;">Numero de expediente:</p>
        <p style="text-align: justify"><b><b>${numeroExpediente}</b></b></p>
        <br></br>
        <p style="text-align: justify;">Tipo de actuación:</p>
        <p style="text-align: justify;"><b>INFORME MENSUAL</b></p>
        <br></br>
        <p style="text-align: justify;">Mes y ano: </p>
        <p style="text-align: justify;"><b>${mesAnio}</b></p>
        <br></br>
        <p style="text-align: justify;">Fecha y hora de generacion:</p>
        <p style="text-align: justify;"><b>${fechaHoraGeneracion}</b></p>
        <br></br>
        <p style="text-align: justify;">Numero acuse:</p>
        <p style="text-align: justify;"><b>${numeroAcuse}</b></p>
        <br></br>
        <p style="text-align: justify;">Cuenta de correo electrónico determinado por el prestador de servicios: </p>
        <p style="text-align: justify;"><b>${correo}</b></p>
        <br></br>
    </div>
</div>

<div class="row">
    <div class="col-98">
        <table>
            <tr>
                <td colspan="8" class="gray-header"><p class="text-center"><b>RESUMEN DE MOVIMIENTOS</b></p></td>
            </tr>
            <tr>
                <td colspan="2" class="text-center graysi-header"><b>PERSONAL</b></td>
                <td colspan="2" class="text-center graysi-header"><b>CLIENTES</b></td>
                <td colspan="2" class="text-center graysi-header"><b>VEHICULOS</b></td>
                <td colspan="2" class="text-center graysi-header"><b>EQUIPO</b></td>
            </tr>

            <tr>
                <td>Activos</td>
                <td>${personalActivos}</td>
                <td>Activos</td>
                <td>${clientesActivos}</td>
                <td>Activos</td>
                <td>${vehiculosActivos}</td>
                <td>Activos</td>
                <td>${equipoActivos}</td>
            </tr>

            <tr>
                <td>Altas</td>
                <td>${personalAltas}</td>
                <td>Altas</td>
                <td>${clientesAltas}</td>
                <td>Altas</td>
                <td>${vehiculosAltas}</td>
                <td>Altas</td>
                <td>${equipoAltas}</td>
            </tr>

            <tr>
                <td>Bajas</td>
                <td>${personalBajas}</td>
                <td>Bajas</td>
                <td>${clientesBajas}</td>
                <td>Bajas</td>
                <td>${vehiculosBajas}</td>
                <td>Bajas</td>
                <td>${equipoBajas}</td>
            </tr>

            <tr>
                <td><b>TOTAL</b></td>
                <td>${personalTotal}</td>
                <td><b>TOTAL</b></td>
                <td>${clientesTotal}</td>
                <td><b>TOTAL</b></td>
                <td>${vehiculosTotal}</td>
                <td><b>TOTAL</b></td>
                <td>${equipoTotal}</td>
            </tr>
        </table>
        <br></br>
        <table>
            <tr>
                <td colspan="4" class="gray-header"><b class="text-center"><p class="text-center"><b>INCIDENCIAS</b></p></b></td>
            </tr>
            <tr>
                <td class="text-center graysi-header"><b>ABIERTAS</b></td>
                <td class="text-center graysi-header"><b>PROCEDENTES</b></td>
                <td class="text-center graysi-header"><b>IMPROCEDENTES</b></td>
                <td class="text-center graysi-header"><b>REPORTADAS</b></td>
            </tr>
            <tr>
                <td class="text-center">${incidenciasReportadas}</td>
                <td class="text-center">${incidenciasProcedentes}</td>
                <td class="text-center">${incidenciasImprocedentes}</td>
                <td class="text-center">${incidenciasTotal}</td>
            </tr>
        </table>
        <br></br>
        <#if tieneArmas == "true">
            <table>
                <tr>
                    <td colspan="6" class="gray-header"><b class="text-center"><p class="text-center"><b>FORMA DE EJECUCION ARMADA</b></p></b></td>
                </tr>
                <tr>
                    <td colspan="2" class="graysi-header">
                        <p class="text-center"><b>I. SEGURIDAD PRIVADA EN LOS BIENES</b></p>
                    </td>
                    <td colspan="2" class="graysi-header">
                        <p class="text-center"><b>II. SEGURIDAD PRIVADA A PERSONAS</b></p>
                    </td>
                    <td colspan="2" class="graysi-header">
                        <p class="text-center"><b>III. SEGURIDAD PRIVADA EN EL TRASLADO DE BIENES O VALORES</b></p>
                    </td>
                </tr>
                <tr>
                    <td>Activas</td>
                    <td>${armas1Activas}</td>
                    <td>Activas</td>
                    <td>${armas2Activas}</td>
                    <td>Activas</td>
                    <td>${armas3Activas}</td>
                </tr>
                <tr>
                    <td>Altas</td>
                    <td>${armas1Altas}</td>
                    <td>Altas</td>
                    <td>${armas2Altas}</td>
                    <td>Altas</td>
                    <td>${armas3Altas}</td>
                </tr>
                <tr>
                    <td>Bajas</td>
                    <td>${armas1Bajas}</td>
                    <td>Bajas</td>
                    <td>${armas2Bajas}</td>
                    <td>Bajas</td>
                    <td>${armas3Bajas}</td>
                </tr>
                <tr>
                    <td>TOTAL</td>
                    <td>${armas1Total}</td>
                    <td>TOTAL</td>
                    <td>${armas2Total}</td>
                    <td>TOTAL</td>
                    <td>${armas3Total}</td>
                </tr>
            </table>
            <br></br>
        </#if>
        <#if tieneCanes == "true">
            <table>
                <tr>
                    <td colspan="5" class="gray-header"><b class="text-center"><p class="text-center"><b>FORMA DE EJECUCION CON CANES</b></p></b></td>
                </tr>
                <tr>
                    <td class="graysi-header text-center"><b>ASIGNADOS</b></td>
                    <td class="graysi-header text-center"><b>EN DOMICILIO</b></td>
                    <td class="graysi-header text-center"><b>ALTAS</b></td>
                    <td class="graysi-header text-center"><b>BAJAS</b></td>
                    <td class="graysi-header text-center"><b>TOTAL</b></td>
                </tr>
                <tr>
                    <td class="text-center">${canesAsignados}</td>
                    <td class="text-center">${canesInstalaciones}</td>
                    <td class="text-center">${canesAltas}</td>
                    <td class="text-center">${canesBajas}</td>
                    <td class="text-center">${canesTotal}</td>
                </tr>
            </table>
            <br></br>
        </#if>
    </div>
</div>

<div class="row">
    <div class="col-98">
        <p style="text-align: justify;">El presente acuse electronico se genera en atención a lo dispuesto por los artículos 55 fracciones III y IV y 161 del Reglamento de los Servicios de Seguridad Privada del Estado de Jalisco.</p>
    </div>
</div>
<br></br>

<div class="row">
    <div class="col-98">
        <p>Cadena Original:</p>
        <p><b>${cadenaOriginal}</b></p>
        <br></br>
        <p style="word-break: break-all !important;">Sello:</p>
        <p><b>${sello}</b></p>
        <br></br>
    </div>
</div>

<div class="row">
    <div class="col-98">
        <p><img src="${imagenBase64}" alt="Codigo QR"></img></p>
    </div>
</div>
</body>
</html>