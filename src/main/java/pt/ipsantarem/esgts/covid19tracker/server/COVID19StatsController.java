package pt.ipsantarem.esgts.covid19tracker.server;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import pt.ipsantarem.esgts.covid19tracker.server.models.VirusStatistic;
import pt.ipsantarem.esgts.covid19tracker.server.trees.AVLVirusStatsTreesManager;

import static pt.ipsantarem.esgts.covid19tracker.server.utils.ObjectUtils.requireNonNullElse;
import static pt.ipsantarem.esgts.covid19tracker.server.utils.VirusPredictionUtils.newStatCasesPredict;
import static pt.ipsantarem.esgts.covid19tracker.server.utils.VirusPredictionUtils.totalStatCasesPredict;

/**
 * COVID 19 stats REST API main and only controller.
 */
public class COVID19StatsController {

    // the tree manager.
    public static AVLVirusStatsTreesManager treeManager;

    @OpenApi(
            path = "/api/:country/cases/new",
            method = HttpMethod.GET,
            description = "Gets a list of COVID-19 new cases in a certain country since the beginning of the pandemic. If " +
                    "startDate and endDate query parameters are both supplied, then it gets the new cases in the dates between " +
                    "the said dates.",
            summary = "Get the new COVID-19 cases in a country.",
            pathParams = {
                    @OpenApiParam(name = "country", description = "The country to get the new COVID-19 cases for.")
            },
            queryParams = {
                    @OpenApiParam(name = "startDate", type = Long.class, description = "Date, in milliseconds"),
                    @OpenApiParam(name = "endDate", type = Long.class, description = "Date, in milliseconds")
            },
            responses = {
                    // responses with same status and content type will be auto-grouped to the oneOf composed scheme
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = VirusStatistic.class, isArray = true))
            }
    )
    public static void getNewCases(Context ctx) {
        String startDate = ctx.queryParam("startDate");
        String endDate = ctx.queryParam("endDate");
        String country = ctx.pathParam("country");
        if (startDate == null || endDate == null) {
            ctx.json(treeManager.getNewCasesStats(country));
        } else {
            ctx.json(treeManager.getNewCasesStatsBetweenDates(country, Long.parseLong(startDate), Long.parseLong(endDate)));
        }
    }

    @OpenApi(
            path = "/api/:country/cases/total",
            method = HttpMethod.GET,
            description = "Gets a list of COVID-19 total cases in a certain country since the beginning of the pandemic. If " +
                    "startDate and endDate query parameters are both supplied, then it gets the total cases in the dates between " +
                    "the said dates.",
            summary = "Get the total COVID-19 cases in a country.",
            pathParams = {
                    @OpenApiParam(name = "country", description = "The country to get the total COVID-19 cases for.")
            },
            queryParams = {
                    @OpenApiParam(name = "startDate", type = Long.class, description = "Date, in milliseconds"),
                    @OpenApiParam(name = "endDate", type = Long.class, description = "Date, in milliseconds")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = VirusStatistic.class, isArray = true))
            }
    )
    public static void getTotalCases(Context ctx) {
        String startDate = ctx.queryParam("startDate");
        String endDate = ctx.queryParam("endDate");
        String country = ctx.pathParam("country");
        if (startDate == null || endDate == null) {
            ctx.json(treeManager.getTotalCasesStats(country));
        } else {
            ctx.json(treeManager.getTotalCasesStatsBetweenDates(country, Long.parseLong(startDate), Long.parseLong(endDate)));
        }
    }

    @OpenApi(
            path = "/api/:country/deaths/new",
            method = HttpMethod.GET,
            description = "Gets a list of COVID-19 new deaths in a certain country since the beginning of the pandemic. If " +
                    "startDate and endDate query parameters are both supplied, then it gets the new deaths in the dates between " +
                    "the said dates.",
            summary = "Get the new COVID-19 deaths in a country.",
            pathParams = {
                    @OpenApiParam(name = "country", description = "The country to get the new COVID-19 deaths for.")
            },
            queryParams = {
                    @OpenApiParam(name = "startDate", type = Long.class, description = "Date, in milliseconds"),
                    @OpenApiParam(name = "endDate", type = Long.class, description = "Date, in milliseconds")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = VirusStatistic.class, isArray = true))
            }
    )
    public static void getNewDeaths(Context ctx) {
        String startDate = ctx.queryParam("startDate");
        String endDate = ctx.queryParam("endDate");
        String country = ctx.pathParam("country");
        if (startDate == null || endDate == null) {
            ctx.json(treeManager.getNewDeathsStats(country));
        } else {
            ctx.json(treeManager.getNewDeathsStatsBetweenDates(country, Long.parseLong(startDate), Long.parseLong(endDate)));
        }
    }

    @OpenApi(
            path = "/api/:country/deaths/total",
            method = HttpMethod.GET,
            description = "Gets a list of COVID-19 total deaths in a certain country since the beginning of the pandemic. If " +
                    "startDate and endDate query parameters are both supplied, then it gets the total deaths in the dates between " +
                    "the said dates.",
            summary = "Get the total COVID-19 deaths in a country.",
            pathParams = {
                    @OpenApiParam(name = "country", description = "The country to get the total COVID-19 deaths for.")
            },
            queryParams = {
                    @OpenApiParam(name = "startDate", type = Long.class, description = "Date, in milliseconds"),
                    @OpenApiParam(name = "endDate", type = Long.class, description = "Date, in milliseconds")
            },
            responses = {
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = VirusStatistic.class, isArray = true))
            }
    )
    public static void getTotalDeaths(Context ctx) {
        String startDate = ctx.queryParam("startDate");
        String endDate = ctx.queryParam("endDate");
        String country = ctx.pathParam("country");
        if (startDate == null || endDate == null) {
            ctx.json(treeManager.getTotalDeathsStats(country));
        } else {
            ctx.json(treeManager.getTotalDeathsStatsBetweenDates(country, Long.parseLong(startDate), Long.parseLong(endDate)));
        }
    }

    @OpenApi(
            path = "/api/:country/cases/new/predict",
            method = HttpMethod.GET,
            description = "Get a prediction of new COVID-19 cases for the next day in a certain country. This works by using " +
                    "a simple linear regression model to estimate the new cases for the next day, according to the trend of the " +
                    "latest 15 days.",
            summary = "Get a prediction of new COVID-19 cases for the next day in a country.",
            pathParams = {
                    @OpenApiParam(name = "country", description = "The country to get a prediction for.")
            },
            responses = {
                    // responses with same status and content type will be auto-grouped to the oneOf composed scheme
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = Integer.class))
            }
    )
    public static void getNewCasesPredict(Context ctx) {
        ctx.json(newStatCasesPredict(treeManager.getTotalCasesStats(ctx.pathParam("country"))));
    }

    @OpenApi(
            path = "/api/:country/cases/total/predict",
            method = HttpMethod.GET,
            description = "Get a prediction of total COVID-19 cases for the next day in a certain country. This works by using " +
                    "a simple linear regression model to estimate the total cases for the next day, according to the trend of the " +
                    "latest 15 days.",
            summary = "Get a prediction of total COVID-19 cases for the next day in a country.",
            pathParams = {
                    @OpenApiParam(name = "country", description = "The country to get a prediction for.")
            },
            responses = {
                    // responses with same status and content type will be auto-grouped to the oneOf composed scheme
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = Integer.class))
            }
    )
    public static void getTotalCasesPredict(Context ctx) {
        ctx.json(totalStatCasesPredict(treeManager.getTotalCasesStats(ctx.pathParam("country"))));
    }

    @OpenApi(
            path = "/api/:country/deaths/new/predict",
            method = HttpMethod.GET,
            description = "Get a prediction of new COVID-19 deaths for the next day in a certain country. This works by using " +
                    "a simple linear regression model to estimate the new deaths for the next day, according to the trend of the " +
                    "latest 15 days.",
            summary = "Get a prediction of new COVID-19 deaths for the next day in a country.",
            pathParams = {
                    @OpenApiParam(name = "country", description = "The country to get a prediction for.")
            },
            responses = {
                    // responses with same status and content type will be auto-grouped to the oneOf composed scheme
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = Integer.class))
            }
    )
    public static void getNewDeathsPredict(Context ctx) {
        ctx.json(newStatCasesPredict(treeManager.getTotalDeathsStats(ctx.pathParam("country"))));
    }

    @OpenApi(
            path = "/api/:country/deaths/total/predict",
            method = HttpMethod.GET,
            description = "Get a prediction of total COVID-19 deaths for the next day in a certain country. This works by using " +
                    "a simple linear regression model to estimate the total deaths for the next day, according to the trend of the " +
                    "latest 15 days.",
            summary = "Get a prediction of total COVID-19 deaths for the next day in a country.",
            pathParams = {
                    @OpenApiParam(name = "country", description = "The country to get a prediction for.")
            },
            responses = {
                    // responses with same status and content type will be auto-grouped to the oneOf composed scheme
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = Integer.class))
            }
    )
    public static void getTotalDeathsPredict(Context ctx) {
        ctx.json(totalStatCasesPredict(treeManager.getTotalDeathsStats(ctx.pathParam("country"))));
    }

    @OpenApi(
            path = "/api/:country/cases/new/:date",
            method = HttpMethod.GET,
            summary = "Get the new COVID-19 cases in a certain day for a country.",
            pathParams = {
                    @OpenApiParam(name = "country", description = "The country to get the new COVID-19 cases in a day for."),
                    @OpenApiParam(name = "date", description = "The date to get new COVID-19 cases for, in milliseconds.")
            },
            responses = {
                    // responses with same status and content type will be auto-grouped to the oneOf composed scheme
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = VirusStatistic.class))
            }
    )
    public static void getNewCasesInDate(Context ctx) {
        VirusStatistic<Integer> stat = treeManager.getNewCasesInDate(ctx.pathParam("country"),
                Long.parseLong(ctx.pathParam("date")));
        ctx.json(requireNonNullElse(stat, ""));
    }

    @OpenApi(
            path = "/api/:country/cases/total/:date",
            method = HttpMethod.GET,
            summary = "Get the total COVID-19 cases in a certain day for a country.",
            pathParams = {
                    @OpenApiParam(name = "country", description = "The country to get the total COVID-19 cases in a day for."),
                    @OpenApiParam(name = "date", description = "The date to get COVID-19 total cases for, in milliseconds.")
            },
            responses = {
                    // responses with same status and content type will be auto-grouped to the oneOf composed scheme
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = VirusStatistic.class))
            }
    )
    public static void getTotalCasesInDate(Context ctx) {
        VirusStatistic<Integer> stat = treeManager.getTotalCasesInDate(ctx.pathParam("country"),
                Long.parseLong(ctx.pathParam("date")));
        ctx.json(requireNonNullElse(stat, ""));
    }

    @OpenApi(
            path = "/api/:country/deaths/new/:date",
            method = HttpMethod.GET,
            summary = "Get the new COVID-19 deaths in a certain day for a country.",
            pathParams = {
                    @OpenApiParam(name = "country", description = "The country to get the new COVID-19 deaths in a day for."),
                    @OpenApiParam(name = "date", description = "The date to get new COVID-19 deaths for, in milliseconds.")
            },
            responses = {
                    // responses with same status and content type will be auto-grouped to the oneOf composed scheme
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = VirusStatistic.class))
            }
    )
    public static void getNewDeathsInDate(Context ctx) {
        VirusStatistic<Integer> stat = treeManager.getNewDeathsInDate(ctx.pathParam("country"),
                Long.parseLong(ctx.pathParam("date")));
        ctx.json(requireNonNullElse(stat, ""));
    }

    @OpenApi(
            path = "/api/:country/deaths/total/:date",
            method = HttpMethod.GET,
            summary = "Get the total COVID-19 deaths in a certain day for a country.",
            pathParams = {
                    @OpenApiParam(name = "country", description = "The country to get the total COVID-19 deaths in a day for."),
                    @OpenApiParam(name = "date", description = "The date to get total COVID-19 deaths for, in milliseconds.")

            },
            responses = {
                    // responses with same status and content type will be auto-grouped to the oneOf composed scheme
                    @OpenApiResponse(status = "200", content = @OpenApiContent(from = VirusStatistic.class))
            }
    )
    public static void getTotalDeathsInDate(Context ctx) {
        VirusStatistic<Integer> stat = treeManager.getTotalDeathsInDate(ctx.pathParam("country"),
                Long.parseLong(ctx.pathParam("date")));
        ctx.json(requireNonNullElse(stat, ""));
    }
}
